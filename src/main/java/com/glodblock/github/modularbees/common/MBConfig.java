package com.glodblock.github.modularbees.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MBConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue OVERCLOCKER_POWER_USAGE = BUILDER
            .comment("Overclocker energy usage per tick.")
            .defineInRange("general.overclocker_energy_usage", 100, 0, 20000);
    public static final ModConfigSpec.IntValue STACKER_MULTIPLIER = BUILDER
            .comment("Stacker slot size multiplier.")
            .defineInRange("general.stacker_multiplier", 3, 2, 10);
    public static final ModConfigSpec.IntValue AUTO_EXPORT_INTERVAL = BUILDER
            .comment("Tick interval for export hatch auto output.")
            .defineInRange("hive.auto_output_interval", 8, 1, 1000);
    public static final ModConfigSpec.IntValue AUTO_IMPORT_INTERVAL = BUILDER
            .comment("Tick interval for import hatch auto input.")
            .defineInRange("hive.auto_input_interval", 8, 1, 1000);
    public static final ModConfigSpec.IntValue HONEY_PRODUCE_BASE = BUILDER
            .comment("The base honey output amount for every bee.")
            .defineInRange("hive.honey_base", 300, 0, 1000);
    public static final ModConfigSpec.IntValue DRAGON_BREATH_PRODUCE_BASE = BUILDER
            .comment("The base dragon breath output amount for draconic bee.")
            .defineInRange("hive.dragon_breath_base", 400, 0, 1000);
    public static final ModConfigSpec.IntValue GEARBOX_WAX = BUILDER
            .comment("The wax amount centrifuge gearbox needs to run.")
            .defineInRange("centrifuge.gearbox_wax", 40, 0, 200);
    public static final ModConfigSpec.DoubleValue GEARBOX_BOOST = BUILDER
            .comment("The boost centrifuge gearbox gives.")
            .defineInRange("centrifuge.gearbox_boost", 1.5, 1, 2);
    public static final ModConfigSpec SPEC = BUILDER.build();

}
