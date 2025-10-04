package com.glodblock.github.modularbees.jei;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.recipe.ElectrodeRecipe;
import com.glodblock.github.modularbees.common.recipe.TreaterRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
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
        assert Minecraft.getInstance().level != null;
        var manager = Minecraft.getInstance().level.getRecipeManager();
        registry.addRecipes(MBTreaterCategory.RECIPE_TYPE, this.getRecipes(TreaterRecipe.TYPE, manager));
        registry.addRecipes(MBElectrodeCategory.RECIPE_TYPE, this.getRecipes(ElectrodeRecipe.TYPE, manager));
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(MBSingletons.MODULAR_TREATER, MBTreaterCategory.RECIPE_TYPE);
        registry.addRecipeCatalyst(MBSingletons.MODULAR_OVERCLOCKER, MBElectrodeCategory.RECIPE_TYPE);
    }

    private <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> getRecipes(RecipeType<T> type, RecipeManager manager) {
        return manager.getAllRecipesFor(type);
    }

}
