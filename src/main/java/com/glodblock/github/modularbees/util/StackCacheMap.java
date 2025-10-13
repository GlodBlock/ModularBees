package com.glodblock.github.modularbees.util;

import cy.jdkdigital.productivebees.common.item.Honeycomb;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import cy.jdkdigital.productivebees.init.ModItems;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class StackCacheMap {

    private final Object2IntMap<ResourceLocation> comb = new Object2IntOpenHashMap<>();
    private final Object2IntMap<ItemStack> nbtItem = new Object2IntOpenCustomHashMap<>(GameUtil.ITEM_HASH);
    private final Map<Item, Integer> item = new IdentityHashMap<>();
    private final RandomSource random;

    public StackCacheMap(RandomSource random) {
        this.random = random;
    }

    public void add(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        final int amt = stack.getCount();
        if (stack.getItem() instanceof Honeycomb) {
            var type = stack.get(ModDataComponents.BEE_TYPE);
            if (type != null) {
                this.comb.computeInt(type, (k, v) -> v == null ? amt : v + amt);
                return;
            }
        }
        if (stack.isComponentsPatchEmpty()) {
            this.item.compute(stack.getItem(), (k, v) -> v == null ? amt : v + amt);
            return;
        }
        this.nbtItem.compute(stack.copyWithCount(1), (k, v) -> v == null ? amt : v + amt);
    }

    public List<ItemStack> getItems(boolean blockMode, float multiplier) {
        List<ItemStack> stacks = new ArrayList<>();
        var item = blockMode ? ModItems.CONFIGURABLE_COMB_BLOCK : ModItems.CONFIGURABLE_HONEYCOMB;
        int absoluteMulti = (int) multiplier;
        float partialMultiplier = multiplier - absoluteMulti;
        for (var entry : this.comb.object2IntEntrySet()) {
            int amt = entry.getIntValue();
            var comb = new ItemStack(item, amt * absoluteMulti);
            if (this.random.nextFloat() <= partialMultiplier) {
                comb.grow(amt);
            }
            comb.set(ModDataComponents.BEE_TYPE, entry.getKey());
            stacks.add(comb);
        }
        for (var entry : this.nbtItem.object2IntEntrySet()) {
            int amt = entry.getIntValue();
            var stack = entry.getKey().copyWithCount(amt * absoluteMulti);
            if (this.random.nextFloat() <= partialMultiplier) {
                stack.grow(amt);
            }
            stacks.add(stack);
        }
        for (var entry : this.item.entrySet()) {
            ItemLike key = entry.getKey();
            if (blockMode) {
                if (key == ModItems.HONEYCOMB_GHOSTLY.get()) {
                    key = ModBlocks.COMB_GHOSTLY.get();
                } else if (key == ModItems.HONEYCOMB_MILKY.get()) {
                    key = ModBlocks.COMB_MILKY.get();
                } else if (key == ModItems.HONEYCOMB_POWDERY.get()) {
                    key = ModBlocks.COMB_POWDERY.get();
                } else if (key == Items.HONEYCOMB) {
                    key = Blocks.HONEYCOMB_BLOCK;
                }
            }
            int amt = entry.getValue();
            var comb = new ItemStack(key, amt * absoluteMulti);
            if (this.random.nextFloat() <= partialMultiplier) {
                comb.grow(amt);
            }
            stacks.add(comb);
        }
        return stacks;
    }


}
