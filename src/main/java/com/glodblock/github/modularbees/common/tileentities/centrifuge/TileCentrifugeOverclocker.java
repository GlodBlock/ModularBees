package com.glodblock.github.modularbees.common.tileentities.centrifuge;

import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.tileentities.base.TileMBOverclocker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileCentrifugeOverclocker extends TileMBOverclocker {

    public TileCentrifugeOverclocker(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public Direction getFacing() {
        return MBSingletons.MODULAR_CENTRIFUGE_OVERCLOCKER.get().getFacing(this.getBlockState());
    }

    @Override
    public Component getDisplayName() {
        return MBSingletons.MODULAR_CENTRIFUGE_OVERCLOCKER.get().getName();
    }

}
