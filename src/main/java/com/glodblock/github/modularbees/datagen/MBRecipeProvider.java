package com.glodblock.github.modularbees.datagen;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.common.recipe.TreaterRecipe;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class MBRecipeProvider extends RecipeProvider {

    static String C = "has_item";

    public MBRecipeProvider(PackOutput pack, CompletableFuture<HolderLookup.Provider> lookup) {
        super(pack, lookup);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput c) {
        TreaterRecipe.builder()
                .input(ModItems.HONEY_TREAT.get())
                .boost(1.6f)
                .save(c, ModularBees.id("treater/honey_treat"));
        TreaterRecipe.builder()
                .input(ModItems.HONEY_BUCKET.get())
                .output(Items.BUCKET)
                .boost(5)
                .save(c, ModularBees.id("treater/honey_bucket"));
        TreaterRecipe.builder()
                .input(Items.HONEY_BOTTLE)
                .output(Items.GLASS_BOTTLE)
                .boost(1.2f)
                .save(c, ModularBees.id("treater/honey_bottle"));
        TreaterRecipe.builder()
                .input(Blocks.HONEY_BLOCK)
                .boost(5)
                .save(c, ModularBees.id("treater/honey_block"));
        TreaterRecipe.builder()
                .input(Items.SUGAR)
                .boost(1.3f)
                .save(c, ModularBees.id("treater/sugar"));
    }

}
