package com.glodblock.github.modularbees.common.tileentities.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class TileMBModularComponent extends TileMBBase {

    @Nullable
    protected TileMBModularCore core;

    public TileMBModularComponent(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void linkCore(TileMBModularCore core) {
        this.core = core;
    }

    public boolean isActive() {
        if (this.core != null && !this.core.isRemoved()) {
            return this.core.isFormed();
        }
        return false;
    }

}
