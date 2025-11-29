package com.glodblock.github.modularbees.datagen;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.recipe.ElectrodeRecipe;
import com.glodblock.github.modularbees.common.recipe.TreaterRecipe;
import com.glodblock.github.modularbees.util.GameConstants;
import com.glodblock.github.modularbees.util.MBTags;
import cy.jdkdigital.productivebees.common.recipe.BottlerRecipe;
import cy.jdkdigital.productivebees.datagen.recipe.builder.CentrifugeRecipeBuilder;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivelib.common.recipe.TagOutputRecipe;
import cy.jdkdigital.productivelib.registry.LibItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
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
                .boost(1.06f)
                .save(c, ModularBees.id("treater/honey_treat"));
        TreaterRecipe.builder()
                .input(ModItems.HONEY_BUCKET.get())
                .output(Items.BUCKET)
                .boost(1.15f)
                .save(c, ModularBees.id("treater/honey_bucket"));
        TreaterRecipe.builder()
                .input(Items.HONEY_BOTTLE)
                .output(Items.GLASS_BOTTLE)
                .boost(1.03f)
                .save(c, ModularBees.id("treater/honey_bottle"));
        TreaterRecipe.builder()
                .input(Blocks.HONEY_BLOCK)
                .boost(1.15f)
                .save(c, ModularBees.id("treater/honey_block"));
        TreaterRecipe.builder()
                .input(MBSingletons.HONEY_JELLY)
                .boost(1.4f)
                .save(c, ModularBees.id("treater/honey_jelly"));
        TreaterRecipe.builder()
                .input(MBSingletons.LOYAL_TREAT)
                .boost(2f)
                .save(c, ModularBees.id("treater/loyal_treat"));
        TreaterRecipe.builder()
                .input(MBSingletons.ENDER_TREAT)
                .boost(3f)
                .save(c, ModularBees.id("treater/ender_treat"));
        TreaterRecipe.builder()
                .input(MBSingletons.SOUL_TREAT)
                .boost(4.5f)
                .save(c, ModularBees.id("treater/soul_treat"));
        ElectrodeRecipe.builder()
                .input(MBSingletons.ELECTRODE_COPPER)
                .power(1.2f)
                .save(c, ModularBees.id("electrode/electrode_copper"));
        ElectrodeRecipe.builder()
                .input(MBSingletons.ELECTRODE_IRON)
                .power(1.5f)
                .save(c, ModularBees.id("electrode/electrode_iron"));
        ElectrodeRecipe.builder()
                .input(MBSingletons.ELECTRODE_GOLD)
                .power(2.8f)
                .save(c, ModularBees.id("electrode/electrode_gold"));
        ElectrodeRecipe.builder()
                .input(MBSingletons.ELECTRODE_NETHERITE)
                .power(5f)
                .save(c, ModularBees.id("electrode/electrode_netherite"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MBSingletons.SCENTED_PLANK, 3)
                .pattern("WHW")
                .pattern("PPP")
                .pattern("WHW")
                .define('W', ModItems.WAX.get())
                .define('H', MBSingletons.HONEY_JELLY)
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
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_BEEHIVE_OVERCLOCKER)
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
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_BEEHIVE_EXPORT)
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
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_BEEHIVE_STACKER)
                .pattern("CPC")
                .pattern("LAL")
                .define('C', Tags.Items.CHESTS)
                .define('A', LibItems.UPGRADE_BLOCK.get())
                .define('P', MBSingletons.MODULAR_BEEHIVE_PART)
                .define('L', Tags.Items.LEATHERS)
                .unlockedBy(C, has(MBSingletons.MODULAR_BEEHIVE_PART))
                .save(c, ModularBees.id("modular_beehive_stacker"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MBSingletons.LOYAL_TREAT)
                .requires(ModItems.HONEY_TREAT.get())
                .requires(MBSingletons.HONEY_JELLY)
                .requires(MBSingletons.HONEY_JELLY)
                .requires(MBSingletons.HONEY_JELLY)
                .requires(DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, new PotionContents(Potions.HEALING), Items.POTION))
                .unlockedBy(C, has(MBSingletons.HONEY_JELLY))
                .save(c, ModularBees.id("royal_treat"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MBSingletons.ENDER_TREAT)
                .requires(MBSingletons.LOYAL_TREAT)
                .requires(MBSingletons.LOYAL_TREAT)
                .requires(MBSingletons.HONEY_JELLY)
                .requires(Items.DRAGON_BREATH)
                .requires(ModItems.DRACONIC_CHUNK.get())
                .requires(ModItems.DRACONIC_CHUNK.get())
                .requires(DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, new PotionContents(Potions.SWIFTNESS), Items.POTION))
                .unlockedBy(C, has(MBSingletons.LOYAL_TREAT))
                .save(c, ModularBees.id("ender_treat"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MBSingletons.SOUL_TREAT)
                .requires(MBSingletons.ENDER_TREAT)
                .requires(MBSingletons.ENDER_TREAT)
                .requires(MBSingletons.LOYAL_TREAT)
                .requires(Blocks.SCULK)
                .requires(Blocks.SCULK)
                .requires(Blocks.SCULK_CATALYST)
                .requires(DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, new PotionContents(Potions.REGENERATION), Items.POTION))
                .unlockedBy(C, has(MBSingletons.ENDER_TREAT))
                .save(c, ModularBees.id("soul_treat"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_CENTRIFUGE_CORE)
                .pattern("N1N")
                .pattern("WCW")
                .pattern("N2N")
                .define('N', Tags.Items.INGOTS_NETHERITE)
                .define('1', LibItems.UPGRADE_ENTITY_FILTER.get())
                .define('2', ModBlocks.INACTIVE_DRAGON_EGG.get())
                .define('C', ModBlocks.CENTRIFUGE.get())
                .define('W', MBTags.WAX_BLOCK)
                .unlockedBy(C, has(ModBlocks.CENTRIFUGE.get()))
                .save(c, ModularBees.id("modular_centrifuge_core"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_CENTRIFUGE_PART)
                .pattern("BSB")
                .pattern("WCW")
                .pattern("BSB")
                .define('S', ModItems.OBSIDIAN_SHARD.get())
                .define('W', ModItems.WITHER_SKULL_CHIP.get())
                .define('C', Blocks.CAULDRON)
                .define('B', Blocks.IRON_BARS)
                .unlockedBy(C, has(Blocks.CAULDRON))
                .save(c, ModularBees.id("modular_centrifuge_part"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_CENTRIFUGE_OVERCLOCKER)
                .pattern("BCB")
                .pattern("RPR")
                .pattern("BXB")
                .define('C', Items.CLOCK)
                .define('B', Blocks.IRON_BARS)
                .define('R', Items.REPEATER)
                .define('P', MBSingletons.MODULAR_CENTRIFUGE_PART)
                .define('X', Items.COMPARATOR)
                .unlockedBy(C, has(MBSingletons.MODULAR_CENTRIFUGE_PART))
                .save(c, ModularBees.id("modular_centrifuge_overclocker"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_CENTRIFUGE_EXPORT)
                .pattern("IPI")
                .pattern(" H ")
                .define('I', Ingredient.of(Blocks.PISTON, Blocks.STICKY_PISTON))
                .define('H', Blocks.HOPPER)
                .define('P', MBSingletons.MODULAR_CENTRIFUGE_PART)
                .unlockedBy(C, has(MBSingletons.MODULAR_CENTRIFUGE_PART))
                .save(c, ModularBees.id("modular_centrifuge_export"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_CENTRIFUGE_IMPORT)
                .pattern("IPI")
                .pattern(" C ")
                .define('I', Ingredient.of(Blocks.PISTON, Blocks.STICKY_PISTON))
                .define('C', Tags.Items.CHESTS)
                .define('P', MBSingletons.MODULAR_CENTRIFUGE_PART)
                .unlockedBy(C, has(MBSingletons.MODULAR_CENTRIFUGE_PART))
                .save(c, ModularBees.id("modular_centrifuge_import"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_CENTRIFUGE_HEATER)
                .pattern("N1N")
                .pattern("NPN")
                .pattern("N2N")
                .define('N', Tags.Items.INGOTS_NETHERITE)
                .define('1', ModBlocks.HEATED_CENTRIFUGE.get())
                .define('2', LibItems.UPGRADE_BLOCK.get())
                .define('P', MBSingletons.MODULAR_CENTRIFUGE_PART)
                .unlockedBy(C, has(ModBlocks.HEATED_CENTRIFUGE.get()))
                .save(c, ModularBees.id("modular_centrifuge_heater"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_CENTRIFUGE_GEARBOX)
                .pattern("GRG")
                .pattern("XPX")
                .pattern("GCG")
                .define('G', Blocks.GRINDSTONE)
                .define('R', Items.COMPARATOR)
                .define('X', Tags.Items.CHAINS)
                .define('P', MBSingletons.MODULAR_CENTRIFUGE_PART)
                .define('C', Tags.Items.CHESTS)
                .unlockedBy(C, has(MBSingletons.MODULAR_CENTRIFUGE_PART))
                .save(c, ModularBees.id("modular_centrifuge_gearbox"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, MBSingletons.BEE_EXTRACTOR)
                .pattern("RPR")
                .pattern("S S")
                .pattern(" B ")
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('P', Ingredient.of(Blocks.PISTON, Blocks.STICKY_PISTON))
                .define('S', Tags.Items.STONES)
                .define('B', ModBlocks.BOTTLER.get())
                .unlockedBy(C, has(ModBlocks.BOTTLER.get()))
                .save(c, ModularBees.id("bee_extractor"));
        this.electrode(MBSingletons.ELECTRODE_COPPER, Tags.Items.INGOTS_COPPER, Tags.Items.STORAGE_BLOCKS_COPPER, "copper", c);
        this.electrode(MBSingletons.ELECTRODE_IRON, Tags.Items.INGOTS_IRON, Tags.Items.STORAGE_BLOCKS_IRON, "iron", c);
        this.electrode(MBSingletons.ELECTRODE_GOLD, Tags.Items.INGOTS_GOLD, Tags.Items.STORAGE_BLOCKS_GOLD, "gold", c);
        SmithingTransformRecipeBuilder.smithing(
                Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                Ingredient.of(MBSingletons.ELECTRODE_GOLD),
                Ingredient.of(Blocks.NETHERITE_BLOCK),
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
        CentrifugeRecipeBuilder.item(Blocks.HONEY_BLOCK.asItem())
                .setFluidOutput(new FluidStack(Fluids.WATER, 20))
                .addOutput(new TagOutputRecipe.ChancedOutput(Ingredient.of(MBSingletons.HONEY_JELLY), 1, 1, 0.1F))
                .save(c, ModularBees.id("centrifuge/honey_jelly"));
    }

    private void electrode(Item electrode, TagKey<Item> material, TagKey<Item> material2, String name, @NotNull RecipeOutput c) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, electrode)
                .pattern(" BG")
                .pattern(" ZB")
                .pattern("M  ")
                .define('B', Tags.Items.DYES_BLACK)
                .define('G', Tags.Items.NUGGETS_GOLD)
                .define('M', material)
                .define('Z', material2)
                .unlockedBy(C, has(material))
                .save(c, ModularBees.id("electrode_" + name));
    }

    private void bottle(@NotNull RecipeOutput c, BottlerRecipe recipe, ResourceLocation id) {
        c.accept(id, recipe, null);
    }

}
