package com.glodblock.github.modularbees.common;

import com.glodblock.github.modularbees.common.blocks.centrifuge.BlockCentrifugePart;
import com.glodblock.github.modularbees.common.blocks.centrifuge.BlockModularCentrifuge;
import com.glodblock.github.modularbees.common.blocks.hive.BlockBeehiveAlveary;
import com.glodblock.github.modularbees.common.blocks.hive.BlockBeehiveDragon;
import com.glodblock.github.modularbees.common.blocks.hive.BlockBeehiveExport;
import com.glodblock.github.modularbees.common.blocks.hive.BlockBeehiveFeeder;
import com.glodblock.github.modularbees.common.blocks.hive.BlockBeehiveOverclocker;
import com.glodblock.github.modularbees.common.blocks.hive.BlockBeehivePart;
import com.glodblock.github.modularbees.common.blocks.hive.BlockBeehiveTreater;
import com.glodblock.github.modularbees.common.blocks.hive.BlockModularBeehive;
import com.glodblock.github.modularbees.common.blocks.misc.BlockFluidDragonBreath;
import com.glodblock.github.modularbees.common.blocks.misc.BlockScentedPlank;
import com.glodblock.github.modularbees.common.fluids.FluidDragonBreath;
import com.glodblock.github.modularbees.common.items.ItemElectrode;
import com.glodblock.github.modularbees.common.tileentities.centrifuge.TileCentrifugePart;
import com.glodblock.github.modularbees.common.tileentities.centrifuge.TileModularCentrifuge;
import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveAlveary;
import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveDragon;
import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveExport;
import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveFeeder;
import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveOverclocker;
import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehivePart;
import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveTreater;
import com.glodblock.github.modularbees.common.tileentities.hive.TileModularBeehive;
import com.glodblock.github.modularbees.util.GameConstants;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

public class MBSingletons {

    public static BlockModularBeehive MODULAR_BEEHIVE_CORE;
    public static BlockBeehivePart MODULAR_BEEHIVE_PART;
    public static BlockBeehiveAlveary MODULAR_ALVEARY;
    public static BlockBeehiveFeeder MODULAR_FEEDER;
    public static BlockBeehiveOverclocker MODULAR_OVERCLOCKER;
    public static BlockBeehiveTreater MODULAR_TREATER;
    public static BlockBeehiveExport MODULAR_EXPORT;
    public static BlockBeehiveDragon MODULAR_DRAGON_HIVE;
    public static BlockScentedPlank SCENTED_PLANK;
    public static BlockFluidDragonBreath DRAGON_BREATH;
    public static BlockModularCentrifuge MODULAR_CENTRIFUGE_CORE;
    public static BlockCentrifugePart MODULAR_CENTRIFUGE_PART;

    public static ItemElectrode ELECTRODE_COPPER;
    public static ItemElectrode ELECTRODE_IRON;
    public static ItemElectrode ELECTRODE_GOLD;
    public static ItemElectrode ELECTRODE_NETHERITE;
    public static Item DRAGON_BREATH_BUCKET;

    public static void init(MBRegistryHandler regHandler) {
        MODULAR_BEEHIVE_CORE = new BlockModularBeehive();
        MODULAR_BEEHIVE_PART = new BlockBeehivePart();
        MODULAR_ALVEARY = new BlockBeehiveAlveary();
        MODULAR_FEEDER = new BlockBeehiveFeeder();
        MODULAR_OVERCLOCKER = new BlockBeehiveOverclocker();
        MODULAR_TREATER = new BlockBeehiveTreater();
        MODULAR_EXPORT = new BlockBeehiveExport();
        MODULAR_DRAGON_HIVE = new BlockBeehiveDragon();
        MODULAR_CENTRIFUGE_CORE = new BlockModularCentrifuge();
        MODULAR_CENTRIFUGE_PART = new BlockCentrifugePart();
        SCENTED_PLANK = new BlockScentedPlank();
        DRAGON_BREATH = new BlockFluidDragonBreath();
        ELECTRODE_COPPER = new ItemElectrode(5 * GameConstants.MINUTE, Ingredient.of(Tags.Items.INGOTS_COPPER));
        ELECTRODE_IRON = new ItemElectrode(15 * GameConstants.MINUTE, Ingredient.of(Tags.Items.INGOTS_IRON));
        ELECTRODE_GOLD = new ItemElectrode(12 * GameConstants.MINUTE, Ingredient.of(Tags.Items.INGOTS_GOLD));
        ELECTRODE_NETHERITE = new ItemElectrode(25 * GameConstants.MINUTE, Ingredient.of(Tags.Items.INGOTS_NETHERITE));
        DRAGON_BREATH_BUCKET = new BucketItem(FluidDragonBreath.getFluid(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
        regHandler.block("modular_beehive_core", MODULAR_BEEHIVE_CORE, TileModularBeehive.class, TileModularBeehive::new);
        regHandler.block("modular_beehive_part", MODULAR_BEEHIVE_PART, TileBeehivePart.class, TileBeehivePart::new);
        regHandler.block("modular_beehive_alveary", MODULAR_ALVEARY, TileBeehiveAlveary.class, TileBeehiveAlveary::new);
        regHandler.block("modular_beehive_feeder", MODULAR_FEEDER, TileBeehiveFeeder.class, TileBeehiveFeeder::new);
        regHandler.block("modular_beehive_overclocker", MODULAR_OVERCLOCKER, TileBeehiveOverclocker.class, TileBeehiveOverclocker::new);
        regHandler.block("modular_treater", MODULAR_TREATER, TileBeehiveTreater.class, TileBeehiveTreater::new);
        regHandler.block("modular_export", MODULAR_EXPORT, TileBeehiveExport.class, TileBeehiveExport::new);
        regHandler.block("modular_dragon_hive", MODULAR_DRAGON_HIVE, TileBeehiveDragon.class, TileBeehiveDragon::new);
        regHandler.block("modular_centrifuge_core", MODULAR_CENTRIFUGE_CORE, TileModularCentrifuge.class, TileModularCentrifuge::new);
        regHandler.block("modular_centrifuge_part", MODULAR_CENTRIFUGE_PART, TileCentrifugePart.class, TileCentrifugePart::new);
        regHandler.block("scented_plank", SCENTED_PLANK);
        regHandler.item("electrode_copper", ELECTRODE_COPPER);
        regHandler.item("electrode_iron", ELECTRODE_IRON);
        regHandler.item("electrode_gold", ELECTRODE_GOLD);
        regHandler.item("electrode_netherite", ELECTRODE_NETHERITE);
        regHandler.item("dragon_breath_bucket", DRAGON_BREATH_BUCKET);
    }

}
