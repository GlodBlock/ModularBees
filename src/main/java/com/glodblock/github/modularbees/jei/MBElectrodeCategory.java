package com.glodblock.github.modularbees.jei;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.gui.MBBaseGui;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.recipe.ElectrodeRecipe;
import com.glodblock.github.modularbees.util.GameUtil;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

public class MBElectrodeCategory extends MBRecipeCategory<ElectrodeRecipe> {

    public static RecipeType<RecipeHolder<ElectrodeRecipe>> RECIPE_TYPE = RecipeType.createFromVanilla(ElectrodeRecipe.TYPE);

    public MBElectrodeCategory(IGuiHelper helpers) {
        super(helpers, RECIPE_TYPE, MBSingletons.MODULAR_OVERCLOCKER, 110, 18);
        this.background = helpers
                .drawableBuilder(ModularBees.id("textures/gui/jei_overclocker.png"), 0, 0, 110, 18)
                .setTextureSize(110, 18)
                .build();
    }

    @Override
    void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull ElectrodeRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addInputSlot(12, 1).addItemStack(recipe.electrode()).setSlotName("input");
    }

    @Override
    public void draw(@NotNull ElectrodeRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("modularbees.jei.overclocker_boost", GameUtil.NUMBER_F.format(recipe.power())), 33, 5, MBBaseGui.DEFAULT_TEXT_COLOR, false);
    }

}
