package com.glodblock.github.modularbees.common.items;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class ItemElectrode extends ItemMB {

    private final Ingredient type;

    public ItemElectrode(int durability, Ingredient type) {
        super(new Properties().durability(durability));
        this.type = type;
    }

    public ItemElectrode(int durability) {
        this(durability, Ingredient.EMPTY);
    }

    @Override
    public int getEnchantmentValue(@NotNull ItemStack stack) {
        return 1;
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack electrode, @NotNull ItemStack repair) {
        return this.type.test(repair);
    }

}
