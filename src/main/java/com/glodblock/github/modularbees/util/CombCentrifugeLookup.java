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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public final class CombCentrifugeLookup {

    private static final Object2ReferenceMap<RK, Output> RL_MAP = new Object2ReferenceOpenHashMap<>();
    private static final IdentityHashMap<Item, Output> ITEM_MAP = new IdentityHashMap<>();
    private static final Object2ReferenceMap<ItemStack, Output> NBT_MAP = new Object2ReferenceOpenCustomHashMap<>(GameUtil.ITEM_HASH);
    private static final Set<Item> VALID_INPUT = Collections.newSetFromMap(new IdentityHashMap<>());
    private static boolean needInit = true;
    private static final Consumer<ItemStack> PREPROCESS = CombCentrifugeLookup::filterWax;

    private CombCentrifugeLookup() {
        // NO-OP
    }

    public static boolean validInput(ItemStack stack, Level world) {
        if (stack.isEmpty() || world == null) {
            return false;
        }
        if (stack.is(ModTags.Common.HONEYCOMBS) || stack.is(ModTags.Common.STORAGE_BLOCK_HONEYCOMBS)) {
            return true;
        }
        if (needInit) {
            needInit = false;
            for (var holder : world.getRecipeManager().byType(ModRecipeTypes.CENTRIFUGE_TYPE.get())) {
                var input = holder.value().ingredient;
                for (var item : input.getItems()) {
                    if (!stack.is(ModTags.Common.HONEYCOMBS) && !stack.is(ModTags.Common.STORAGE_BLOCK_HONEYCOMBS)) {
                        VALID_INPUT.add(item.getItem());
                    }
                }
            }
        }
        return VALID_INPUT.contains(stack.getItem());
    }

    public static boolean query(Consumer<ItemStack> itemAcceptor, Consumer<FluidStack> fluidAcceptor, ItemStack comb, @NotNull Level world, int para, boolean heated) {
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
        if (cache == null) {
            cache = lookupRecipe(comb, world);
        }
        if (cache != null) {
            //cache.info();
            cache.output(fluidAcceptor, para);
            if (heated) {
                cache.output(PREPROCESS.andThen(itemAcceptor), para, world.getRandom());
            } else {
                cache.output(itemAcceptor, para, world.getRandom());
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

    @Nullable
    private static Output lookupRecipe(@NotNull ItemStack comb, @NotNull Level world) {
        ItemStack key = comb.copy();
        boolean isBlock = comb.is(ModTags.Common.STORAGE_BLOCK_HONEYCOMBS);
        if (isBlock) {
            key = BeeHelper.getSingleComb(comb);
        }
        if (key.isEmpty()) {
            return null;
        }
        var inv = new InventoryHandlerHelper.BlockEntityItemStackHandler(2);
        inv.setStackInSlot(1, key);
        var recipe = BeeHelper.getCentrifugeRecipe(world, inv);
        if (recipe == null) {
            return null;
        }
        var output = new Output(
                recipe.value()
                        .getRecipeOutputs()
                        .entrySet()
                        .stream()
                        .map(e -> ChanceStack.of(e.getKey(), mul4(e.getValue(), isBlock)))
                        .toList(),
                mul4(recipe.value().getFluidOutputs(), isBlock)
        );
        var item = key.getItem();
        if (item instanceof Honeycomb) {
            var type = key.get(ModDataComponents.BEE_TYPE);
            if (type != null) {
                RL_MAP.put(new RK(type, isBlock), output);
            } else {
                NBT_MAP.put(comb, output);
            }
        } else if (comb.isComponentsPatchEmpty()) {
            ITEM_MAP.put(comb.getItem(), output);
        } else {
            NBT_MAP.put(comb, output);
        }
        return output;
    }

    private static TagOutputRecipe.ChancedOutput mul4(TagOutputRecipe.ChancedOutput origin, boolean enable) {
        if (!enable) {
            return origin;
        }
        float chance = origin.chance();
        if (chance * 4 <= 1) {
            return new TagOutputRecipe.ChancedOutput(origin.ingredient(), origin.min(), origin.max(), chance * 4);
        } else if (chance * 2 <= 1) {
            return new TagOutputRecipe.ChancedOutput(origin.ingredient(), origin.min() * 2, origin.max() * 2, chance * 2);
        } else {
            return new TagOutputRecipe.ChancedOutput(origin.ingredient(), origin.min() * 4, origin.max() * 4, chance);
        }
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

    private record Output(List<ChanceStack> outputs, FluidStack fluid) {

        @Override
        public @NotNull String toString() {
            var sb = new StringBuilder();
            for (var stack : this.outputs) {
                sb.append("Type: ").append(stack.getClass().getSimpleName()).append(" ");
                sb.append("Stack: %s, Avg: %s, P: %s".formatted(stack.getBaseStack().getItem(), stack.getAverageAmount(), stack.getChance()));
            }
            return sb.toString();
        }

        void output(Consumer<ItemStack> collector, int multiplier, RandomSource random) {
            // If N is small, use brute simulation
            if (multiplier <= 8) {
                this.outputs.forEach(c -> {
                    for (int i = 0; i < multiplier; i ++) {
                        c.get(collector, random);
                    }
                });
            } else {
                // If N is large, use normal distribution simulation
                this.outputs.forEach(c -> this.roll(collector, c, multiplier, random));
            }
        }

        void output(Consumer<FluidStack> collector, int multiplier) {
            if (!this.fluid.isEmpty()) {
                collector.accept(this.fluid.copyWithAmount(this.fluid.getAmount() * multiplier));
            }
        }

        void roll(Consumer<ItemStack> collector, ChanceStack chance, int n, RandomSource random) {
            var stack = chance.getBaseStack();
            float w = chance.getAverageAmount();
            float p = chance.getChance();
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
