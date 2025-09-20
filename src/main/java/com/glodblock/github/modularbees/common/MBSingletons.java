package com.glodblock.github.modularbees.common;

import com.glodblock.github.modularbees.common.blocks.hive.BlockBeehiveAlveary;
import com.glodblock.github.modularbees.common.blocks.hive.BlockBeehiveExport;
import com.glodblock.github.modularbees.common.blocks.hive.BlockBeehiveFeeder;
import com.glodblock.github.modularbees.common.blocks.hive.BlockBeehiveOverclocker;
import com.glodblock.github.modularbees.common.blocks.hive.BlockBeehivePart;
import com.glodblock.github.modularbees.common.blocks.hive.BlockBeehiveTreater;
import com.glodblock.github.modularbees.common.blocks.hive.BlockModularBeehive;
import com.glodblock.github.modularbees.common.blocks.misc.BlockScentedPlank;
import com.glodblock.github.modularbees.common.items.ItemElectrode;
import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveAlveary;
import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveExport;
import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveFeeder;
import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveOverclocker;
import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehivePart;
import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveTreater;
import com.glodblock.github.modularbees.common.tileentities.hive.TileModularBeehive;
import com.glodblock.github.modularbees.util.GameConstants;
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
    public static BlockScentedPlank SCENTED_PLANK;

    public static ItemElectrode ELECTRODE_COPPER;
    public static ItemElectrode ELECTRODE_IRON;
    public static ItemElectrode ELECTRODE_GOLD;
    public static ItemElectrode ELECTRODE_NETHERITE;

    public static void init(MBRegistryHandler regHandler) {
        MODULAR_BEEHIVE_CORE = new BlockModularBeehive();
        MODULAR_BEEHIVE_PART = new BlockBeehivePart();
        MODULAR_ALVEARY = new BlockBeehiveAlveary();
        MODULAR_FEEDER = new BlockBeehiveFeeder();
        MODULAR_OVERCLOCKER = new BlockBeehiveOverclocker();
        MODULAR_TREATER = new BlockBeehiveTreater();
        MODULAR_EXPORT = new BlockBeehiveExport();
        ELECTRODE_COPPER = new ItemElectrode(3 * GameConstants.MINUTE, 1.8f, Ingredient.of(Tags.Items.INGOTS_COPPER));
        ELECTRODE_IRON = new ItemElectrode(10 * GameConstants.MINUTE, 3, Ingredient.of(Tags.Items.INGOTS_IRON));
        ELECTRODE_GOLD = new ItemElectrode(10 * GameConstants.MINUTE, 8, Ingredient.of(Tags.Items.INGOTS_GOLD));
        ELECTRODE_NETHERITE = new ItemElectrode(25 * GameConstants.MINUTE, 25, Ingredient.of(Tags.Items.INGOTS_NETHERITE));
        SCENTED_PLANK = new BlockScentedPlank();
        regHandler.block("modular_beehive_core", MODULAR_BEEHIVE_CORE, TileModularBeehive.class, TileModularBeehive::new);
        regHandler.block("modular_beehive_part", MODULAR_BEEHIVE_PART, TileBeehivePart.class, TileBeehivePart::new);
        regHandler.block("modular_beehive_alveary", MODULAR_ALVEARY, TileBeehiveAlveary.class, TileBeehiveAlveary::new);
        regHandler.block("modular_beehive_feeder", MODULAR_FEEDER, TileBeehiveFeeder.class, TileBeehiveFeeder::new);
        regHandler.block("modular_beehive_overclocker", MODULAR_OVERCLOCKER, TileBeehiveOverclocker.class, TileBeehiveOverclocker::new);
        regHandler.block("modular_treater", MODULAR_TREATER, TileBeehiveTreater.class, TileBeehiveTreater::new);
        regHandler.block("modular_export", MODULAR_EXPORT, TileBeehiveExport.class, TileBeehiveExport::new);
        regHandler.block("scented_plank", SCENTED_PLANK);
        regHandler.item("electrode_copper", ELECTRODE_COPPER);
        regHandler.item("electrode_iron", ELECTRODE_IRON);
        regHandler.item("electrode_gold", ELECTRODE_GOLD);
        regHandler.item("electrode_netherite", ELECTRODE_NETHERITE);
    }

}
