package com.glodblock.github.modularbees.common.tileentities.hive;

import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.common.MBSingletons;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileBeehiveStacker extends TileBeehivePart {

    public TileBeehiveStacker(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileBeehiveStacker.class, TileBeehiveStacker::new, MBSingletons.MODULAR_BEEHIVE_STACKER), pos, state);
    }

}
