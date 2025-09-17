package com.glodblock.github.modularbees.common.items;

import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveOverclocker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class ItemElectrode extends ItemMB implements TileBeehiveOverclocker.HiveElectrode {

    private final float power;
    private final Ingredient type;

    public ItemElectrode(int durability, float power, Ingredient type) {
        super(new Properties().durability(durability));
        this.power = power;
        this.type = type;
    }

    public ItemElectrode(int durability, float power) {
        this(durability, power, Ingredient.EMPTY);
    }

    @Override
    public float getPower() {
        return this.power;
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
