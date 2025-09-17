package com.glodblock.github.modularbees.common.caps;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public interface FluidHandlerHost {

    default IFluidHandler getFluidInventory(Direction side) {
        return this.getFluidInventory();
    }

    IFluidHandler getFluidInventory();

}
