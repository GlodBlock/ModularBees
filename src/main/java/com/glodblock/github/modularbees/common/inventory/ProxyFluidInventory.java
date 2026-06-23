package com.glodblock.github.modularbees.common.inventory;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public record ProxyFluidInventory(Supplier<IFluidHandler> getter) implements IFluidHandler {

    @Override
    public int getTanks() {
        return this.getter.get().getTanks();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return this.getter.get().getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return this.getter.get().getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return this.getter.get().isFluidValid(tank, stack);
    }

    @Override
    public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
        return this.getter.get().fill(resource, action);
    }

    @Override
    public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
        return this.getter.get().drain(resource, action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
        return this.getter.get().drain(maxDrain, action);
    }

}