package com.glodblock.github.modularbees.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public final class MBTags {

    public static final TagKey<Fluid> DRAGON_BREATH = FluidTags.create(ResourceLocation.fromNamespaceAndPath("c", "dragon_breath"));
    public static final TagKey<Item> WAX_BLOCK = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/wax"));
    // Third-party tags
    public static final TagKey<Item> WRENCH = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "tools/wrench"));
    public static final TagKey<Block> SOUL_BLACKLIST = BlockTags.create(ResourceLocation.fromNamespaceAndPath("industrialforegoingsouls", "cant_accelerate"));
    public static final TagKey<Block> JDT_BLACKLIST = BlockTags.create(ResourceLocation.fromNamespaceAndPath("justdirethings", "tick_speed_deny"));
    public static final TagKey<Block> TIAB_BLACKLIST = BlockTags.create(ResourceLocation.fromNamespaceAndPath("tiab", "un_acceleratable"));

}
