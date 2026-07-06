package com.glodblock.github.modularbees.common.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Repairable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemElectrode extends ItemMB {

    public ItemElectrode(Properties properties, int durability, @Nullable Ingredient type) {
        super(applyRepair(properties.durability(durability).enchantable(1), type));
    }

    public ItemElectrode(Properties properties, int durability, TagKey<@NotNull Item> type) {
        super(properties.durability(durability).enchantable(1).repairable(type));
    }

    public ItemElectrode(Properties properties, int durability) {
        super(properties.durability(durability).enchantable(1));
    }

    private static Properties applyRepair(Properties properties, Ingredient type) {
        if (type != null) {
            properties.component(DataComponents.REPAIRABLE, new Repairable(type.getValues()));
        }
        return properties;
    }

}
