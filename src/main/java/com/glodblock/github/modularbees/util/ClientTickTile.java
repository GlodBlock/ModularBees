package com.glodblock.github.modularbees.util;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ClientTickTile {

    void tickClient(Level world, BlockState state);

}

