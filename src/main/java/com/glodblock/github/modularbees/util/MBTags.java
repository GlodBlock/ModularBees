package com.glodblock.github.modularbees.util;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public final class MBTags {

    public static final TagKey<@NotNull Fluid> DRAGON_BREATH = FluidTags.create(Identifier.fromNamespaceAndPath("c", "dragon_breath"));
    public static final TagKey<@NotNull Item> WAX_BLOCK = ItemTags.create(Identifier.fromNamespaceAndPath("c", "storage_blocks/wax"));
    // Third-party tags
    public static final TagKey<@NotNull Item> WRENCH = ItemTags.create(Identifier.fromNamespaceAndPath("c", "tools/wrench"));

}
