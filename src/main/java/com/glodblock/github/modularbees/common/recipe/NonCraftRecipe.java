package com.glodblock.github.modularbees.common.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface NonCraftRecipe extends Recipe<@NotNull RecipeInput> {

    @Override
    default boolean isSpecial() {
        return true;
    }

    @Override
    default boolean showNotification() {
        return false;
    }

    @Override
    default @NotNull String group() {
        return "";
    }

    @Override
    default @NotNull PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    default @NotNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    default boolean matches(RecipeInput input, @NotNull Level level) {
        return false;
    }

    @Override
    default @NotNull ItemStack assemble(RecipeInput input) {
        return ItemStack.EMPTY;
    }

}
