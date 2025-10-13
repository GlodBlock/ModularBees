package com.glodblock.github.modularbees.util;

import cy.jdkdigital.productivebees.common.item.CombBlockItem;
import cy.jdkdigital.productivebees.common.item.Honeycomb;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import cy.jdkdigital.productivebees.util.BeeHelper;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.common.recipe.TagOutputRecipe;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Consumer;

public final class CombCentrifugeLookup {

    private static final Object2ReferenceMap<ResourceLocation, Output> RL_MAP = new Object2ReferenceOpenHashMap<>();
    private static final IdentityHashMap<Item, Output> ITEM_MAP = new IdentityHashMap<>();
    private static final Object2ReferenceMap<ItemStack, Output> NBT_MAP = new Object2ReferenceOpenCustomHashMap<>(GameUtil.ITEM_HASH);

    private CombCentrifugeLookup() {

    }

    public static boolean query(Consumer<ItemStack> accepter, ItemStack comb, @NotNull Level world) {
        if (comb.isEmpty()) {
            return false;
        }
        var item = comb.getItem();
        Output cache;
        if (item instanceof Honeycomb || item instanceof CombBlockItem) {
            var type = comb.get(ModDataComponents.BEE_TYPE);
            if (type != null) {
                cache = RL_MAP.get(type);
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
            cache.output(accepter, world.getRandom());
            return true;
        }
        return false;
    }

    @Nullable
    private static Output lookupRecipe(@NotNull ItemStack comb, @NotNull Level world) {
        ItemStack key = comb.copy();
        boolean isBlock = comb.getItem() instanceof BlockItem;
        if (comb.getItem() instanceof BlockItem) {
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
                        .toList()
        );
        var item = key.getItem();
        if (item instanceof Honeycomb) {
            var type = key.get(ModDataComponents.BEE_TYPE);
            if (type != null) {
                RL_MAP.put(type, output);
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
        if (chance * 4 < 1) {
            return new TagOutputRecipe.ChancedOutput(origin.ingredient(), origin.min(), origin.max(), chance * 4);
        } else if (chance * 2 < 1) {
            return new TagOutputRecipe.ChancedOutput(origin.ingredient(), origin.min() * 2, origin.max() * 2, chance * 2);
        } else {
            return new TagOutputRecipe.ChancedOutput(origin.ingredient(), origin.min() * 4, origin.max() * 4, chance);
        }
    }

    private static void clear() {
        RL_MAP.clear();
        NBT_MAP.clear();
        ITEM_MAP.clear();
    }

    private record Output(List<ChanceStack> outputs) {

        void output(Consumer<ItemStack> collector, RandomSource random) {
            this.outputs.forEach(c -> c.get(collector, random));
        }

    }

    @SubscribeEvent
    public static void onReload(OnDatapackSyncEvent event) {
        clear();
    }

}
