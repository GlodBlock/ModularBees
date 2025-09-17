package com.glodblock.github.modularbees.client.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

public interface ConnectBlock {

    boolean canConnect(BlockGetter world, BlockPos otherPos);

}
