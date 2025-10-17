package com.glodblock.github.modularbees.common.tileentities.hive;

import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.tileentities.base.TileMBOverclocker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class TileBeehiveOverclocker extends TileMBOverclocker {

    public TileBeehiveOverclocker(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileBeehiveOverclocker.class, TileBeehiveOverclocker::new, MBSingletons.MODULAR_BEEHIVE_OVERCLOCKER), pos, state);
    }

    @Override
    public Direction getFacing() {
        return MBSingletons.MODULAR_BEEHIVE_OVERCLOCKER.getFacing(this.getBlockState());
    }

    @Override
    public Component getDisplayName() {
        return MBSingletons.MODULAR_BEEHIVE_OVERCLOCKER.getName();
    }

}
