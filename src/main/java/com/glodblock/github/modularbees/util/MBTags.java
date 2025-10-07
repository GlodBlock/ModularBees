package com.glodblock.github.modularbees.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

public final class MBTags {

    public static final TagKey<Fluid> DRAGON_BREATH = FluidTags.create(ResourceLocation.fromNamespaceAndPath("c", "dragon_breath"));

    public static final TagKey<Item> WRENCH = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "tools/wrench"));

}
