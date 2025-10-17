package com.glodblock.github.modularbees.common.inventory;

import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public interface RandomAccessTank extends IFluidHandler {

    RandomAccessTank EMPTY = new EmptyTank();
    FluidTank EMPTY_TANK = new FluidTank(0);

    IFluidTank getTank(int tank);

    class EmptyTank extends EmptyFluidHandler implements RandomAccessTank {

        @Override
        public IFluidTank getTank(int tank) {
            return EMPTY_TANK;
        }

    }

}
