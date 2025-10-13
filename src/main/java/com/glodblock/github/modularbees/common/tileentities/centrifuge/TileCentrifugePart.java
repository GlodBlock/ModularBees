package com.glodblock.github.modularbees.common.tileentities.centrifuge;

import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.tileentities.base.TileMBModularComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileCentrifugePart extends TileMBModularComponent {

    public TileCentrifugePart(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public TileCentrifugePart(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileCentrifugePart.class, TileCentrifugePart::new, MBSingletons.MODULAR_CENTRIFUGE_PART), pos, state);
    }

}