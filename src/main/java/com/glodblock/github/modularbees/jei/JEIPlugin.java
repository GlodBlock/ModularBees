package com.glodblock.github.modularbees.jei;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.gui.MBBaseGui;
import com.glodblock.github.modularbees.client.util.SyncRecipes;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.recipe.ElectrodeRecipe;
import com.glodblock.github.modularbees.common.recipe.TreaterRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    @Override
    public @NotNull Identifier getPluginUid() {
        return ModularBees.id("jei_plugin");
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registry) {
        var helpers = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new MBTreaterCategory(helpers));
        registry.addRecipeCategories(new MBElectrodeCategory(helpers));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registry) {
        registry.addRecipes(MBTreaterCategory.RECIPE_TYPE, this.getRecipes(TreaterRecipe.TYPE));
        registry.addRecipes(MBElectrodeCategory.RECIPE_TYPE, this.getRecipes(ElectrodeRecipe.TYPE));
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registry) {
        registry.addCraftingStation(MBTreaterCategory.RECIPE_TYPE, MBSingletons.MODULAR_TREATER);
        registry.addCraftingStation(MBElectrodeCategory.RECIPE_TYPE, MBSingletons.MODULAR_BEEHIVE_OVERCLOCKER, MBSingletons.MODULAR_CENTRIFUGE_OVERCLOCKER);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(MBBaseGui.class, new GhostSlotHandler<>());
    }

    private <I extends RecipeInput, T extends Recipe<@NotNull I>> List<RecipeHolder<@NotNull T>> getRecipes(RecipeType<@NotNull T> type) {
        return SyncRecipes.INSTANCE.byType(type).stream().toList();
    }

}
