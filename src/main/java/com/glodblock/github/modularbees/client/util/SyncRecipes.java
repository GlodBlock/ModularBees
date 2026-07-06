package com.glodblock.github.modularbees.client.util;

import com.glodblock.github.modularbees.common.recipe.ElectrodeRecipe;
import com.glodblock.github.modularbees.common.recipe.TreaterRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class SyncRecipes {

    public static final SyncRecipes INSTANCE = new SyncRecipes();
    private RecipeMap recipeMap = RecipeMap.EMPTY;

    private SyncRecipes() {
        assert this == INSTANCE;
    }

    @SubscribeEvent
    public void receiveRecipes(RecipesReceivedEvent event) {
        this.recipeMap = event.getRecipeMap();
    }

    @SubscribeEvent
    public void sendSyncRecipe(OnDatapackSyncEvent event) {
        event.sendRecipes(ElectrodeRecipe.TYPE, TreaterRecipe.TYPE);
    }

    public <I extends RecipeInput, T extends Recipe<@NotNull I>> Collection<RecipeHolder<@NotNull T>> byType(RecipeType<@NotNull T> type) {
        return this.recipeMap.byType(type);
    }

}
