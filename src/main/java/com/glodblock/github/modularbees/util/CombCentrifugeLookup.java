package com.glodblock.github.modularbees.util;

import cy.jdkdigital.productivebees.common.item.CombBlockItem;
import cy.jdkdigital.productivebees.common.item.Honeycomb;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeHelper;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.common.recipe.TagOutputRecipe;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public final class CombCentrifugeLookup {

    private static final Object2ReferenceMap<RK, Output> RL_MAP = new Object2ReferenceOpenHashMap<>();
    private static final IdentityHashMap<Item, Output> ITEM_MAP = new IdentityHashMap<>();
    private static final Object2ReferenceMap<ItemStack, Output> NBT_MAP = new Object2ReferenceOpenCustomHashMap<>(GameUtil.ITEM_HASH);
    private static final Set<ItemStack> VALID_INPUT = new ObjectOpenCustomHashSet<>(GameUtil.ITEM_HASH);
    private static boolean needInit = true;
    private static final Consumer<ItemStack> PREPROCESS = CombCentrifugeLookup::filterWax;

    private CombCentrifugeLookup() {
        // NO-OP
    }

    public static boolean validInput(ItemStack stack, Level world) {
        if (stack.isEmpty() || world == null) {
            return false;
        }
        var singleComb = ItemStack.EMPTY;
        if (stack.is(ModTags.Common.STORAGE_BLOCK_HONEYCOMBS)) {
            singleComb = BeeHelper.getSingleComb(stack);
        }
        if (needInit) {
            needInit = false;
            for (var holder : world.getRecipeManager().byType(ModRecipeTypes.CENTRIFUGE_TYPE.get())) {
                var input = holder.value().ingredient;
                for (var item : input.getItems()) {
                    VALID_INPUT.add(item.copy());
                }
            }
        }
        return VALID_INPUT.contains(stack) || VALID_INPUT.contains(singleComb);
    }

    public static boolean query(Consumer<ItemStack> itemAcceptor, Consumer<FluidStack> fluidAcceptor, ItemStack comb, @NotNull Level world, int para, float chanceBoost, boolean heated) {
        if (comb.isEmpty() || para <= 0) {
            return false;
        }
        var item = comb.getItem();
        if (comb.is(ModTags.Common.STORAGE_BLOCK_HONEYCOMBS) && !heated) {
            return false;
        }
        Output cache;
        if (item instanceof Honeycomb || item instanceof CombBlockItem) {
            var type = comb.get(ModDataComponents.BEE_TYPE);
            if (type != null) {
                cache = RL_MAP.get(new RK(type, comb.is(ModTags.Common.STORAGE_BLOCK_HONEYCOMBS)));
            } else {
                cache = NBT_MAP.get(comb);
            }
        } else if (comb.isComponentsPatchEmpty()) {
            cache = ITEM_MAP.get(item);
        } else {
            cache = NBT_MAP.get(comb);
        }
        System.out.println("Input: " + comb);
        if (cache != null) {
            System.out.println("Hit cache: " + cache);
        } else {
            System.out.println("Not hit cache");
        }
        if (cache == null) {
            cache = lookupRecipe(comb, world);
            if (item instanceof Honeycomb || item instanceof CombBlockItem) {
                var type = comb.get(ModDataComponents.BEE_TYPE);
                if (type != null) {
                    RL_MAP.put(new RK(type, comb.is(ModTags.Common.STORAGE_BLOCK_HONEYCOMBS)), cache);
                } else {
                    NBT_MAP.put(comb, cache);
                }
            } else if (comb.isComponentsPatchEmpty()) {
                ITEM_MAP.put(item, cache);
            } else {
                NBT_MAP.put(comb, cache);
            }
        }
        if (cache != Output.EMPTY) {
            cache.output(fluidAcceptor, para);
            if (heated) {
                cache.output(PREPROCESS.andThen(itemAcceptor), para, chanceBoost, world.getRandom());
            } else {
                cache.output(itemAcceptor, para, chanceBoost, world.getRandom());
            }
            return true;
        }
        return false;
    }

    private static void filterWax(ItemStack stack) {
        if (stack.is(ModTags.Common.WAXES)) {
            stack.setCount(0);
        }
    }

    @NotNull
    private static Output lookupRecipe(@NotNull ItemStack comb, @NotNull Level world) {
        ItemStack key = comb.copy();
        ItemStack altKey = ItemStack.EMPTY;
        boolean isBlock = false;
        if (comb.is(ModTags.Common.STORAGE_BLOCK_HONEYCOMBS)) {
            altKey = BeeHelper.getSingleComb(comb);
        }
        if (key.isEmpty() && altKey.isEmpty()) {
            return Output.EMPTY;
        }
        // Lookup direct recipe first
        var inv = new InventoryHandlerHelper.BlockEntityItemStackHandler(2);
        inv.setStackInSlot(1, key);
        var recipe = BeeHelper.getCentrifugeRecipe(world, inv);
        if (recipe == null) {
            // Lookup possible comb recipe
            if (!altKey.isEmpty()) {
                inv.setStackInSlot(1, altKey);
                recipe = BeeHelper.getCentrifugeRecipe(world, inv);
                isBlock = true;
            }
            if (recipe == null) {
                return Output.EMPTY;
            }
        }
        final boolean fBlock = isBlock;
        return new Output(
                recipe.value()
                        .getRecipeOutputs()
                        .entrySet()
                        .stream()
                        .map(e -> BoostChanceStack.of(e.getKey(), mul4(e.getValue(), fBlock)))
                        .toList(),
                mul4(recipe.value().getFluidOutputs(), fBlock)
        );
    }

    private static TagOutputRecipe.ChancedOutput mul4(TagOutputRecipe.ChancedOutput origin, boolean enable) {
        if (!enable) {
            return origin;
        }
        return new TagOutputRecipe.ChancedOutput(origin.ingredient(), origin.min() * 4, origin.max() * 4, origin.chance());
    }

    private static FluidStack mul4(FluidStack origin, boolean enable) {
        if (!enable || origin.isEmpty()) {
            return origin;
        }
        var amt = origin.getAmount();
        return origin.copyWithAmount(amt * 4);
    }

    private static void clear() {
        RL_MAP.clear();
        NBT_MAP.clear();
        ITEM_MAP.clear();
        VALID_INPUT.clear();
        needInit = true;
    }

    private record Output(List<BoostChanceStack> outputs, FluidStack fluid) {

        static final Output EMPTY = new Output(List.of(), FluidStack.EMPTY);

        @Override
        public @NotNull String toString() {
            var sb = new StringBuilder();
            for (var stack : this.outputs) {
                sb.append("Type: ").append(stack.getClass().getSimpleName()).append(" ");
                sb.append("Stack: %s, Avg: %s, P: %s".formatted(stack.getBaseStack().getItem(), stack.getAverageAmount(), stack.getChance()));
            }
            return sb.toString();
        }

        void output(Consumer<ItemStack> collector, int multiplier, float boost, RandomSource random) {
            // If N is small, use brute simulation
            if (multiplier <= 8) {
                this.outputs.forEach(c -> {
                    for (int i = 0; i < multiplier; i ++) {
                        c.get(collector, boost, random);
                    }
                });
            } else {
                // If N is large, use normal distribution simulation
                this.outputs.forEach(c -> this.roll(collector, c, multiplier, boost, random));
            }
        }

        void output(Consumer<FluidStack> collector, int multiplier) {
            if (!this.fluid.isEmpty()) {
                collector.accept(this.fluid.copyWithAmount(this.fluid.getAmount() * multiplier));
            }
        }

        void roll(Consumer<ItemStack> collector, BoostChanceStack chance, int n, float boost, RandomSource random) {
            var stack = chance.getBaseStack();
            float w = chance.getAverageAmount();
            float p = Math.min(chance.getChance() + boost, 1);
            double u = w * n * p;
            double o = Math.sqrt(w * w * n * p * (1 - p));
            int x = (int) Math.round(u + o * random.nextGaussian());
            if (x > 0) {
                collector.accept(stack.copyWithCount(x));
            }
        }

    }

    @SubscribeEvent
    public static void onReload(OnDatapackSyncEvent event) {
        clear();
    }

    record RK(ResourceLocation rl, boolean block) {

    }

}
