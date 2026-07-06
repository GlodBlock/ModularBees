package com.glodblock.github.modularbees.xmod.ae;

import appeng.api.AECapabilities;
import appeng.me.helpers.IGridConnectedBlockEntity;
import com.glodblock.github.modularbees.common.MBRegistryHandler;
import com.glodblock.github.modularbees.xmod.ae.blocks.BlockMECentrifugeExport;
import com.glodblock.github.modularbees.xmod.ae.blocks.BlockMEHiveExport;
import com.glodblock.github.modularbees.xmod.ae.tileentities.TileMECentrifugeExport;
import com.glodblock.github.modularbees.xmod.ae.tileentities.TileMEHiveExport;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.NotNull;

public class AEXSingletons {

    public static DeferredBlock<@NotNull BlockMEHiveExport> ME_BEEHIVE_EXPORT;
    public static DeferredBlock<@NotNull BlockMECentrifugeExport> ME_CENTRIFUGE_EXPORT;

    public static void init(MBRegistryHandler regHandler) {
        ME_BEEHIVE_EXPORT = regHandler.block("me_beehive_export", BlockMEHiveExport::new, TileMEHiveExport.class, TileMEHiveExport::new);
        ME_CENTRIFUGE_EXPORT = regHandler.block("me_centrifuge_export", BlockMECentrifugeExport::new, TileMECentrifugeExport.class, TileMECentrifugeExport::new);
        regHandler.cap(IGridConnectedBlockEntity.class, AECapabilities.IN_WORLD_GRID_NODE_HOST, (object, _) -> object);
    }

}
