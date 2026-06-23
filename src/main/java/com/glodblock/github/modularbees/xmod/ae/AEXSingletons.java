package com.glodblock.github.modularbees.xmod.ae;

import appeng.api.AECapabilities;
import appeng.me.helpers.IGridConnectedBlockEntity;
import com.glodblock.github.modularbees.common.MBRegistryHandler;
import com.glodblock.github.modularbees.xmod.ae.blocks.BlockMECentrifugeExport;
import com.glodblock.github.modularbees.xmod.ae.blocks.BlockMEHiveExport;
import com.glodblock.github.modularbees.xmod.ae.tileentities.TileMECentrifugeExport;
import com.glodblock.github.modularbees.xmod.ae.tileentities.TileMEHiveExport;

public class AEXSingletons {

    public static BlockMEHiveExport ME_BEEHIVE_EXPORT;
    public static BlockMECentrifugeExport ME_CENTRIFUGE_EXPORT;

    public static void init(MBRegistryHandler regHandler) {
        ME_BEEHIVE_EXPORT = new BlockMEHiveExport();
        ME_CENTRIFUGE_EXPORT = new BlockMECentrifugeExport();
        regHandler.block("me_beehive_export", ME_BEEHIVE_EXPORT, TileMEHiveExport.class, TileMEHiveExport::new);
        regHandler.block("me_centrifuge_export", ME_CENTRIFUGE_EXPORT, TileMECentrifugeExport.class, TileMECentrifugeExport::new);
        regHandler.cap(IGridConnectedBlockEntity.class, AECapabilities.IN_WORLD_GRID_NODE_HOST, (object, context) -> object);
    }

}
