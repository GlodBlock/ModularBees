package com.glodblock.github.modularbees.common.tileentities.hive;

import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.tileentities.base.TileMBModularComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileBeehivePart extends TileMBModularComponent {

    public TileBeehivePart(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public TileBeehivePart(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileBeehivePart.class, TileBeehivePart::new, MBSingletons.MODULAR_BEEHIVE_PART), pos, state);
    }

}
