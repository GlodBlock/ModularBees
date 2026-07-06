package com.glodblock.github.modularbees.datagen;

import appeng.core.ConventionTags;
import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.recipe.ElectrodeRecipe;
import com.glodblock.github.modularbees.common.recipe.TreaterRecipe;
import com.glodblock.github.modularbees.util.GameConstants;
import com.glodblock.github.modularbees.util.MBTags;
import com.glodblock.github.modularbees.xmod.ModIDs;
import com.glodblock.github.modularbees.xmod.ae.AEXSingletons;
import cy.jdkdigital.productivebees.common.recipe.BottlerRecipe;
import cy.jdkdigital.productivebees.datagen.recipe.builder.CentrifugeRecipeBuilder;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivelib.common.recipe.TagOutputRecipe;
import cy.jdkdigital.productivelib.registry.LibItems;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class MBRecipeProvider extends RecipeProvider {

    static String C = "has_item";
    protected final HolderGetter<@NotNull Fluid> fluids;

    public MBRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
        this.fluids = registries.lookupOrThrow(Registries.FLUID);
    }

    @Override
    protected void buildRecipes() {
        TreaterRecipe.builder()
                .input(ModItems.HONEY_TREAT.get())
                .boost(1.06f)
                .save(this.output, ModularBees.id("treater/honey_treat"));
        TreaterRecipe.builder()
                .input(ModItems.HONEY_BUCKET.get())
                .output(Items.BUCKET)
                .boost(1.15f)
                .save(this.output, ModularBees.id("treater/honey_bucket"));
        TreaterRecipe.builder()
                .input(Items.HONEY_BOTTLE)
                .output(Items.GLASS_BOTTLE)
                .boost(1.03f)
                .save(this.output, ModularBees.id("treater/honey_bottle"));
        TreaterRecipe.builder()
                .input(Blocks.HONEY_BLOCK)
                .boost(1.15f)
                .save(this.output, ModularBees.id("treater/honey_block"));
        TreaterRecipe.builder()
                .input(MBSingletons.HONEY_JELLY)
                .boost(1.4f)
                .save(this.output, ModularBees.id("treater/honey_jelly"));
        TreaterRecipe.builder()
                .input(MBSingletons.LOYAL_TREAT)
                .boost(2f)
                .save(this.output, ModularBees.id("treater/loyal_treat"));
        TreaterRecipe.builder()
                .input(MBSingletons.ENDER_TREAT)
                .boost(3f)
                .save(this.output, ModularBees.id("treater/ender_treat"));
        TreaterRecipe.builder()
                .input(MBSingletons.SOUL_TREAT)
                .boost(4.5f)
                .save(this.output, ModularBees.id("treater/soul_treat"));
        ElectrodeRecipe.builder()
                .input(MBSingletons.ELECTRODE_COPPER)
                .power(1.2f)
                .save(this.output, ModularBees.id("electrode/electrode_copper"));
        ElectrodeRecipe.builder()
                .input(MBSingletons.ELECTRODE_IRON)
                .power(1.5f)
                .save(this.output, ModularBees.id("electrode/electrode_iron"));
        ElectrodeRecipe.builder()
                .input(MBSingletons.ELECTRODE_GOLD)
                .power(2.8f)
                .save(this.output, ModularBees.id("electrode/electrode_gold"));
        ElectrodeRecipe.builder()
                .input(MBSingletons.ELECTRODE_NETHERITE)
                .power(5f)
                .save(this.output, ModularBees.id("electrode/electrode_netherite"));
        this.shaped(RecipeCategory.BUILDING_BLOCKS, MBSingletons.SCENTED_PLANK, 3)
                .pattern("WHW")
                .pattern("PPP")
                .pattern("WHW")
                .define('W', ModItems.WAX.get())
                .define('H', MBSingletons.HONEY_JELLY)
                .define('P', ItemTags.PLANKS)
                .unlockedBy(C, has(ModItems.WAX.get()))
                .save(this.output);
        this.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_BEEHIVE_CORE)
                .pattern("P1P")
                .pattern("BHB")
                .pattern("P2P")
                .define('P', MBSingletons.SCENTED_PLANK)
                .define('H', ModTags.HIVES)
                .define('B', Blocks.IRON_BARS)
                .define('1', LibItems.UPGRADE_SIMULATOR.get())
                .define('2', LibItems.UPGRADE_ADULT.get())
                .unlockedBy(C, has(ModTags.HIVES))
                .save(this.output);
        this.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_BEEHIVE_PART)
                .pattern("PPP")
                .pattern("PBP")
                .pattern("PPP")
                .define('P', MBSingletons.SCENTED_PLANK)
                .define('B', Blocks.IRON_BARS)
                .unlockedBy(C, has(MBSingletons.SCENTED_PLANK))
                .save(this.output);
        this.shapeless(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_ALVEARY)
                .requires(ModTags.BOXES)
                .requires(MBSingletons.MODULAR_BEEHIVE_PART)
                .unlockedBy(C, has(MBSingletons.MODULAR_BEEHIVE_PART))
                .save(this.output);
        this.shapeless(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_FEEDER)
                .requires(ModBlocks.FEEDER.get())
                .requires(MBSingletons.MODULAR_BEEHIVE_PART)
                .unlockedBy(C, has(MBSingletons.MODULAR_BEEHIVE_PART))
                .save(this.output);
        this.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_BEEHIVE_OVERCLOCKER)
                .pattern("BCB")
                .pattern("RPR")
                .pattern("BXB")
                .define('C', Items.CLOCK)
                .define('B', Blocks.IRON_BARS)
                .define('R', Items.REPEATER)
                .define('P', MBSingletons.MODULAR_BEEHIVE_PART)
                .define('X', Items.COMPARATOR)
                .unlockedBy(C, has(MBSingletons.MODULAR_BEEHIVE_PART))
                .save(this.output);
        this.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_TREATER)
                .pattern("BBB")
                .pattern("GPG")
                .pattern("BCB")
                .define('C', Tags.Items.CHESTS)
                .define('B', Blocks.IRON_BARS)
                .define('G', Items.GLASS_BOTTLE)
                .define('P', MBSingletons.MODULAR_BEEHIVE_PART)
                .unlockedBy(C, has(MBSingletons.MODULAR_BEEHIVE_PART))
                .save(this.output);
        this.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_BEEHIVE_EXPORT)
                .pattern("IPI")
                .pattern(" H ")
                .define('I', Ingredient.of(Blocks.PISTON, Blocks.STICKY_PISTON))
                .define('H', Blocks.HOPPER)
                .define('P', MBSingletons.MODULAR_BEEHIVE_PART)
                .unlockedBy(C, has(MBSingletons.MODULAR_BEEHIVE_PART))
                .save(this.output);
        this.shapeless(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_DRAGON_HIVE)
                .requires(ModBlocks.DRAGON_EGG_HIVE.get())
                .requires(MBSingletons.MODULAR_BEEHIVE_PART)
                .unlockedBy(C, has(ModBlocks.DRAGON_EGG_HIVE.get()))
                .save(this.output);
        this.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_BEEHIVE_STACKER)
                .pattern("CPC")
                .pattern("LAL")
                .define('C', Tags.Items.CHESTS)
                .define('A', LibItems.UPGRADE_BLOCK.get())
                .define('P', MBSingletons.MODULAR_BEEHIVE_PART)
                .define('L', Tags.Items.LEATHERS)
                .unlockedBy(C, has(MBSingletons.MODULAR_BEEHIVE_PART))
                .save(this.output);
        this.shapeless(RecipeCategory.MISC, MBSingletons.LOYAL_TREAT)
                .requires(ModItems.HONEY_TREAT.get())
                .requires(MBSingletons.HONEY_JELLY)
                .requires(MBSingletons.HONEY_JELLY)
                .requires(MBSingletons.HONEY_JELLY)
                .requires(DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, new PotionContents(Potions.HEALING), Items.POTION))
                .unlockedBy(C, has(MBSingletons.HONEY_JELLY))
                .save(this.output);
        this.shapeless(RecipeCategory.MISC, MBSingletons.ENDER_TREAT)
                .requires(MBSingletons.LOYAL_TREAT)
                .requires(MBSingletons.LOYAL_TREAT)
                .requires(MBSingletons.HONEY_JELLY)
                .requires(Items.DRAGON_BREATH)
                .requires(ModItems.DRACONIC_CHUNK.get())
                .requires(ModItems.DRACONIC_CHUNK.get())
                .requires(DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, new PotionContents(Potions.SWIFTNESS), Items.POTION))
                .unlockedBy(C, has(MBSingletons.LOYAL_TREAT))
                .save(this.output);
        this.shapeless(RecipeCategory.MISC, MBSingletons.SOUL_TREAT)
                .requires(MBSingletons.ENDER_TREAT)
                .requires(MBSingletons.ENDER_TREAT)
                .requires(MBSingletons.LOYAL_TREAT)
                .requires(Blocks.SCULK)
                .requires(Blocks.SCULK)
                .requires(Blocks.SCULK_CATALYST)
                .requires(DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, new PotionContents(Potions.REGENERATION), Items.POTION))
                .unlockedBy(C, has(MBSingletons.ENDER_TREAT))
                .save(this.output);
        this.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_CENTRIFUGE_CORE)
                .pattern("N1N")
                .pattern("WCW")
                .pattern("N2N")
                .define('N', Tags.Items.INGOTS_NETHERITE)
                .define('1', LibItems.UPGRADE_ENTITY_FILTER.get())
                .define('2', ModBlocks.INACTIVE_DRAGON_EGG.get())
                .define('C', ModBlocks.CENTRIFUGE.get())
                .define('W', MBTags.WAX_BLOCK)
                .unlockedBy(C, has(ModBlocks.CENTRIFUGE.get()))
                .save(this.output);
        this.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_CENTRIFUGE_PART)
                .pattern("BSB")
                .pattern("WCW")
                .pattern("BSB")
                .define('S', ModItems.OBSIDIAN_SHARD.get())
                .define('W', ModItems.WITHER_SKULL_CHIP.get())
                .define('C', Blocks.CAULDRON)
                .define('B', Blocks.IRON_BARS)
                .unlockedBy(C, has(Blocks.CAULDRON))
                .save(this.output);
        this.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_CENTRIFUGE_OVERCLOCKER)
                .pattern("BCB")
                .pattern("RPR")
                .pattern("BXB")
                .define('C', Items.CLOCK)
                .define('B', Blocks.IRON_BARS)
                .define('R', Items.REPEATER)
                .define('P', MBSingletons.MODULAR_CENTRIFUGE_PART)
                .define('X', Items.COMPARATOR)
                .unlockedBy(C, has(MBSingletons.MODULAR_CENTRIFUGE_PART))
                .save(this.output);
        this.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_CENTRIFUGE_EXPORT)
                .pattern("IPI")
                .pattern(" H ")
                .define('I', Ingredient.of(Blocks.PISTON, Blocks.STICKY_PISTON))
                .define('H', Blocks.HOPPER)
                .define('P', MBSingletons.MODULAR_CENTRIFUGE_PART)
                .unlockedBy(C, has(MBSingletons.MODULAR_CENTRIFUGE_PART))
                .save(this.output);
        this.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_CENTRIFUGE_IMPORT)
                .pattern("IPI")
                .pattern(" C ")
                .define('I', Ingredient.of(Blocks.PISTON, Blocks.STICKY_PISTON))
                .define('C', Tags.Items.CHESTS)
                .define('P', MBSingletons.MODULAR_CENTRIFUGE_PART)
                .unlockedBy(C, has(MBSingletons.MODULAR_CENTRIFUGE_PART))
                .save(this.output);
        this.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_CENTRIFUGE_HEATER)
                .pattern("N1N")
                .pattern("NPN")
                .pattern("N2N")
                .define('N', Tags.Items.INGOTS_NETHERITE)
                .define('1', ModBlocks.HEATED_CENTRIFUGE.get())
                .define('2', LibItems.UPGRADE_BLOCK.get())
                .define('P', MBSingletons.MODULAR_CENTRIFUGE_PART)
                .unlockedBy(C, has(ModBlocks.HEATED_CENTRIFUGE.get()))
                .save(this.output);
        this.shaped(RecipeCategory.DECORATIONS, MBSingletons.MODULAR_CENTRIFUGE_GEARBOX)
                .pattern("GRG")
                .pattern("XPX")
                .pattern("GCG")
                .define('G', Blocks.GRINDSTONE)
                .define('R', Items.COMPARATOR)
                .define('X', Tags.Items.CHAINS)
                .define('P', MBSingletons.MODULAR_CENTRIFUGE_PART)
                .define('C', Tags.Items.CHESTS)
                .unlockedBy(C, has(MBSingletons.MODULAR_CENTRIFUGE_PART))
                .save(this.output);
        this.shaped(RecipeCategory.DECORATIONS, MBSingletons.BEE_EXTRACTOR)
                .pattern("RPR")
                .pattern("S S")
                .pattern(" B ")
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('P', Ingredient.of(Blocks.PISTON, Blocks.STICKY_PISTON))
                .define('S', Tags.Items.STONES)
                .define('B', ModBlocks.BOTTLER.get())
                .unlockedBy(C, has(ModBlocks.BOTTLER.get()))
                .save(this.output);
        this.electrode(MBSingletons.ELECTRODE_COPPER, Tags.Items.INGOTS_COPPER, Tags.Items.STORAGE_BLOCKS_COPPER);
        this.electrode(MBSingletons.ELECTRODE_IRON, Tags.Items.INGOTS_IRON, Tags.Items.STORAGE_BLOCKS_IRON);
        this.electrode(MBSingletons.ELECTRODE_GOLD, Tags.Items.INGOTS_GOLD, Tags.Items.STORAGE_BLOCKS_GOLD);
        SmithingTransformRecipeBuilder.smithing(
                Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                Ingredient.of(MBSingletons.ELECTRODE_GOLD),
                Ingredient.of(Blocks.NETHERITE_BLOCK),
                RecipeCategory.MISC,
                MBSingletons.ELECTRODE_NETHERITE.asItem())
                .unlocks(C, has(MBSingletons.ELECTRODE_GOLD))
                .save(this.output, ModularBees.stringId("electrode_netherite"));
        this.bottle(
                new BottlerRecipe(new SizedFluidIngredient(FluidIngredient.of(this.fluids.getOrThrow(MBTags.DRAGON_BREATH)), GameConstants.BOTTLE), Ingredient.of(Items.GLASS_BOTTLE), new ItemStackTemplate(Items.DRAGON_BREATH)),
                ModularBees.id("bottle/dragon_breath")
        );
        this.shapeless(RecipeCategory.MISC, Items.DRAGON_BREATH, 4)
                .requires(MBSingletons.DRAGON_BREATH_BUCKET)
                .requires(Items.GLASS_BOTTLE, 4)
                .unlockedBy(C, has(MBSingletons.DRAGON_BREATH_BUCKET))
                .save(this.output, ModularBees.stringId("fill_dragon_breath"));
        this.shapeless(RecipeCategory.MISC, MBSingletons.DRAGON_BREATH_BUCKET)
                .requires(Items.BUCKET)
                .requires(Items.DRAGON_BREATH, 4)
                .unlockedBy(C, has(Items.DRAGON_BREATH))
                .save(this.output, ModularBees.stringId("fill_dragon_breath_bucket"));
        CentrifugeRecipeBuilder.item(Blocks.HONEY_BLOCK.asItem())
                .setFluidOutput(Fluids.WATER, 20)
                .addOutput(new TagOutputRecipe.ChancedOutput(Ingredient.of(MBSingletons.HONEY_JELLY), 1, 1, 0.1F))
                .save(this.output, ModularBees.id("centrifuge/honey_jelly"));
        this.shaped(RecipeCategory.MISC, AEXSingletons.ME_BEEHIVE_EXPORT)
                .pattern("FIF")
                .pattern("PSP")
                .define('F', ConventionTags.FLUIX_CRYSTAL)
                .define('I', ConventionTags.INTERFACE)
                .define('P', Items.IRON_BARS)
                .define('S', MBSingletons.MODULAR_BEEHIVE_EXPORT)
                .unlockedBy(C, has(MBSingletons.MODULAR_BEEHIVE_EXPORT))
                .save(this.output.withConditions(mod(ModIDs.AE2)));
        this.shaped(RecipeCategory.MISC, AEXSingletons.ME_CENTRIFUGE_EXPORT)
                .pattern("FIF")
                .pattern("PSP")
                .define('F', ConventionTags.FLUIX_CRYSTAL)
                .define('I', ConventionTags.INTERFACE)
                .define('P', Items.IRON_BARS)
                .define('S', MBSingletons.MODULAR_CENTRIFUGE_EXPORT)
                .unlockedBy(C, has(MBSingletons.MODULAR_CENTRIFUGE_EXPORT))
                .save(this.output.withConditions(mod(ModIDs.AE2)));
    }

    private void electrode(ItemLike electrode, TagKey<@NotNull Item> material, TagKey<@NotNull Item> material2) {
        this.shaped(RecipeCategory.MISC, electrode)
                .pattern(" BG")
                .pattern(" ZB")
                .pattern("M  ")
                .define('B', Tags.Items.DYES_BLACK)
                .define('G', Tags.Items.NUGGETS_GOLD)
                .define('M', material)
                .define('Z', material2)
                .unlockedBy(C, has(material))
                .save(this.output);
    }

    private void bottle(BottlerRecipe recipe, Identifier id) {
        this.output.accept(ResourceKey.create(Registries.RECIPE, id), recipe, null);
    }

    private ICondition mod(String modid) {
        return new ModLoadedCondition(modid);
    }

    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.@NotNull Provider registries, @NotNull RecipeOutput output) {
            return new MBRecipeProvider(registries, output);
        }

        @Override
        public @NotNull String getName() {
            return "Modular Bees Recipes";
        }

    }

}
