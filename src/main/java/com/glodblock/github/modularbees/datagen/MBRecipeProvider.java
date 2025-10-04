package com.glodblock.github.modularbees.datagen;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.recipe.ElectrodeRecipe;
import com.glodblock.github.modularbees.common.recipe.TreaterRecipe;
import com.glodblock.github.modularbees.util.GameConstants;
import com.glodblock.github.modularbees.util.MBTags;
import cy.jdkdigital.productivebees.common.recipe.BottlerRecipe;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivelib.registry.LibItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.TagFluidIngredient;
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
                .boost(2.1f)
                .save(c, ModularBees.id("treater/honey_treat"));
        TreaterRecipe.builder()
                .input(ModItems.HONEY_BUCKET.get())
                .output(Items.BUCKET)
                .boost(4.5f)
                .save(c, ModularBees.id("treater/honey_bucket"));
        TreaterRecipe.builder()
                .input(Items.HONEY_BOTTLE)
                .output(Items.GLASS_BOTTLE)
                .boost(1.6f)
                .save(c, ModularBees.id("treater/honey_bottle"));
        TreaterRecipe.builder()
                .input(Blocks.HONEY_BLOCK)
                .boost(4.5f)
                .save(c, ModularBees.id("treater/honey_block"));
        TreaterRecipe.builder()
                .input(Items.SUGAR)
                .boost(1.1f)
                .save(c, ModularBees.id("treater/sugar"));
        ElectrodeRecipe.builder()
                .input(MBSingletons.ELECTRODE_COPPER)
                .power(1.8f)
                .save(c, ModularBees.id("electrode/electrode_copper"));
        ElectrodeRecipe.builder()
                .input(MBSingletons.ELECTRODE_IRON)
                .power(3f)
                .save(c, ModularBees.id("electrode/electrode_iron"));
        ElectrodeRecipe.builder()
                .input(MBSingletons.ELECTRODE_GOLD)
                .power(8f)
                .save(c, ModularBees.id("electrode/electrode_gold"));
        ElectrodeRecipe.builder()
                .input(MBSingletons.ELECTRODE_NETHERITE)
                .power(25f)
                .save(c, ModularBees.id("electrode/electrode_netherite"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MBSingletons.SCENTED_PLANK, 3)
                .pattern("WHW")
                .pattern("PPP")
                .pattern("WHW")
                .define('W', ModItems.WAX.get())
                .define('H', Blocks.HONEY_BLOCK)
                .define('P', ItemTags.PLANKS)
                .unlockedBy(C, has(ModItems.WAX.get()))
                .save(c, ModularBees.id("scented_plank"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_BEEHIVE_CORE)
                .pattern("P1P")
                .pattern("BHB")
                .pattern("P2P")
                .define('P', MBSingletons.SCENTED_PLANK)
                .define('H', ModTags.HIVES)
                .define('B', Blocks.IRON_BARS)
                .define('1', LibItems.UPGRADE_SIMULATOR.get())
                .define('2', LibItems.UPGRADE_ADULT.get())
                .unlockedBy(C, has(ModTags.HIVES))
                .save(c, ModularBees.id("modular_beehive_core"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_BEEHIVE_PART)
                .pattern("PPP")
                .pattern("PBP")
                .pattern("PPP")
                .define('P', MBSingletons.SCENTED_PLANK)
                .define('B', Blocks.IRON_BARS)
                .unlockedBy(C, has(MBSingletons.SCENTED_PLANK))
                .save(c, ModularBees.id("modular_beehive_part"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_ALVEARY)
                .requires(ModTags.BOXES)
                .requires(MBSingletons.MODULAR_BEEHIVE_PART)
                .unlockedBy(C, has(MBSingletons.MODULAR_BEEHIVE_PART))
                .save(c, ModularBees.id("modular_alveary"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_FEEDER)
                .requires(ModBlocks.FEEDER.get())
                .requires(MBSingletons.MODULAR_BEEHIVE_PART)
                .unlockedBy(C, has(MBSingletons.MODULAR_BEEHIVE_PART))
                .save(c, ModularBees.id("modular_feeder"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_OVERCLOCKER)
                .pattern("BCB")
                .pattern("RPR")
                .pattern("BXB")
                .define('C', Items.CLOCK)
                .define('B', Blocks.IRON_BARS)
                .define('R', Items.REPEATER)
                .define('P', MBSingletons.MODULAR_BEEHIVE_PART)
                .define('X', Items.COMPARATOR)
                .unlockedBy(C, has(MBSingletons.MODULAR_BEEHIVE_PART))
                .save(c, ModularBees.id("modular_overclocker"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_TREATER)
                .pattern("BBB")
                .pattern("GPG")
                .pattern("BCB")
                .define('C', Tags.Items.CHESTS)
                .define('B', Blocks.IRON_BARS)
                .define('G', Items.GLASS_BOTTLE)
                .define('P', MBSingletons.MODULAR_BEEHIVE_PART)
                .unlockedBy(C, has(MBSingletons.MODULAR_BEEHIVE_PART))
                .save(c, ModularBees.id("modular_treater"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_EXPORT)
                .pattern("IPI")
                .pattern(" H ")
                .define('I', Ingredient.of(Blocks.PISTON, Blocks.STICKY_PISTON))
                .define('H', Blocks.HOPPER)
                .define('P', MBSingletons.MODULAR_BEEHIVE_PART)
                .unlockedBy(C, has(MBSingletons.MODULAR_BEEHIVE_PART))
                .save(c, ModularBees.id("modular_export"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_DRAGON_HIVE)
                .requires(ModBlocks.DRAGON_EGG_HIVE.get())
                .requires(MBSingletons.MODULAR_BEEHIVE_PART)
                .unlockedBy(C, has(ModBlocks.DRAGON_EGG_HIVE.get()))
                .save(c, ModularBees.id("modular_dragon_hive"));
        this.electrode(MBSingletons.ELECTRODE_COPPER, Tags.Items.INGOTS_COPPER, "copper", c);
        this.electrode(MBSingletons.ELECTRODE_IRON, Tags.Items.INGOTS_IRON, "iron", c);
        this.electrode(MBSingletons.ELECTRODE_GOLD, Tags.Items.INGOTS_GOLD, "gold", c);
        SmithingTransformRecipeBuilder.smithing(
                Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                Ingredient.of(MBSingletons.ELECTRODE_GOLD),
                Ingredient.of(Items.NETHERITE_INGOT),
                RecipeCategory.MISC,
                MBSingletons.ELECTRODE_NETHERITE)
                .unlocks(C, has(MBSingletons.ELECTRODE_GOLD))
                .save(c, ModularBees.id("electrode_netherite"));
        this.bottle(
                c,
                new BottlerRecipe(new SizedFluidIngredient(new TagFluidIngredient(MBTags.DRAGON_BREATH), GameConstants.BOTTLE), Ingredient.of(Items.GLASS_BOTTLE), new ItemStack(Items.DRAGON_BREATH)),
                ModularBees.id("bottle/dragon_breath")
        );
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.DRAGON_BREATH, 4)
                .requires(MBSingletons.DRAGON_BREATH_BUCKET)
                .requires(Items.GLASS_BOTTLE, 4)
                .unlockedBy(C, has(MBSingletons.DRAGON_BREATH_BUCKET))
                .save(c, ModularBees.id("fill_dragon_breath"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MBSingletons.DRAGON_BREATH_BUCKET)
                .requires(Items.BUCKET)
                .requires(Items.DRAGON_BREATH, 4)
                .unlockedBy(C, has(Items.DRAGON_BREATH))
                .save(c, ModularBees.id("fill_dragon_breath_bucket"));
    }

    private void electrode(Item electrode, TagKey<Item> material, String name, @NotNull RecipeOutput c) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, electrode)
                .pattern(" BG")
                .pattern(" MB")
                .pattern("M  ")
                .define('B', Tags.Items.DYES_BLACK)
                .define('G', Tags.Items.NUGGETS_GOLD)
                .define('M', material)
                .unlockedBy(C, has(material))
                .save(c, ModularBees.id("electrode_" + name));
    }

    private void bottle(@NotNull RecipeOutput c, BottlerRecipe recipe, ResourceLocation id) {
        c.accept(id, recipe, null);
    }

}
