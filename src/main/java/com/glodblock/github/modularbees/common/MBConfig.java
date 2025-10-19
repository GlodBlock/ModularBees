package com.glodblock.github.modularbees.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MBConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue OVERCLOCKER_POWER_USAGE = BUILDER
            .comment("Overclocker energy usage for every bee per tick.")
            .defineInRange("hive.overclocker_energy_usage", 100, 0, 20000);
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
    public static final ModConfigSpec SPEC = BUILDER.build();

}
