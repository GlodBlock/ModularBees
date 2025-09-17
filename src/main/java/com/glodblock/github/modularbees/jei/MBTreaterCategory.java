package com.glodblock.github.modularbees.jei;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.gui.MBBaseGui;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.recipe.TreaterRecipe;
import com.glodblock.github.modularbees.util.GameConstants;
import com.glodblock.github.modularbees.util.GameUtil;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

public class MBTreaterCategory extends MBRecipeCategory<TreaterRecipe> {

    public static RecipeType<RecipeHolder<TreaterRecipe>> RECIPE_TYPE = RecipeType.createFromVanilla(TreaterRecipe.TYPE);
    private final IDrawable arrow;

    public MBTreaterCategory(IGuiHelper helpers) {
        super(helpers, RECIPE_TYPE, MBSingletons.MODULAR_TREATER, 90, 38);
        this.arrow = helpers.createAnimatedRecipeArrow(GameConstants.SECOND * 5);
        this.background = helpers
                .drawableBuilder(ModularBees.id("textures/gui/jei_treater.png"), 0, 0, 90, 38)
                .setTextureSize(90, 38)
                .build();
    }

    @Override
    void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull TreaterRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addInputSlot(12, 1).addIngredients(recipe.input()).setSlotName("input");
        if (!recipe.output().isEmpty()) {
            builder.addOutputSlot(62, 1).addItemStack(recipe.output()).setSlotName("output");
        }
    }

    @Override
    public void draw(@NotNull TreaterRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.arrow.draw(guiGraphics, 33, 1);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("modularbees.jei.treater_boost", GameUtil.NUMBER_F.format((recipe.boost() - 1) * 100) + '%'),0, 23, MBBaseGui.DEFAULT_TEXT_COLOR, false);
    }

}
