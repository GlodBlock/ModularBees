package com.glodblock.github.modularbees.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public abstract class MBRecipeCategory<R extends Recipe<RecipeInput>> extends AbstractRecipeCategory<RecipeHolder<R>> {

    protected IDrawable background;

    public MBRecipeCategory(IGuiHelper helpers, RecipeType<RecipeHolder<R>> recipeType, ItemLike icon, int width, int height) {
        this(helpers, recipeType, new ItemStack(icon), width, height);
    }

    public MBRecipeCategory(IGuiHelper helpers, RecipeType<RecipeHolder<R>> recipeType, ItemStack icon, int width, int height) {
        super(
                recipeType,
                icon.getHoverName(),
                helpers.createDrawableItemStack(icon),
                width,
                height
        );
    }

    abstract void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull R recipe, @NotNull IFocusGroup focuses);

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull RecipeHolder<R> recipe, @NotNull IFocusGroup focuses) {
        this.setRecipe(builder, recipe.value(), focuses);
    }

    protected void draw(R recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {

    }

    @Override
    public void draw(@NotNull RecipeHolder<R> recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
        this.draw(recipe.value(), recipeSlotsView, guiGraphics, mouseX, mouseY);
    }

}
