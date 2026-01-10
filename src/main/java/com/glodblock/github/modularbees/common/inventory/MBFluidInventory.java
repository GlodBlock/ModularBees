package com.glodblock.github.modularbees.common.inventory;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class MBFluidInventory extends FluidTank {

    protected IO mode = IO.ALL;
    protected final BlockEntity host;

    public MBFluidInventory(BlockEntity host, int capacity) {
        super(capacity);
        this.host = host;
    }

    public MBFluidInventory(BlockEntity host, int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
        this.host = host;
    }

    public MBFluidInventory inputOnly() {
        this.mode = IO.IN;
        return this;
    }

    public MBFluidInventory outputOnly() {
        this.mode = IO.OUT;
        return this;
    }

    public int forceFill(@NotNull FluidStack resource, @NotNull FluidAction action) {
        return super.fill(resource, action);
    }

    @Override
    public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
        if (this.mode.canInsert()) {
            return super.fill(resource, action);
        } else {
            return 0;
        }
    }

    @Override
    public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
        if (this.mode.canExtract()) {
            return super.drain(resource, action);
        } else {
            return FluidStack.EMPTY;
        }
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
        if (this.mode.canExtract()) {
            return super.drain(maxDrain, action);
        } else {
            return FluidStack.EMPTY;
        }
    }

    @Override
    protected void onContentsChanged() {
        if (this.host != null) {
            if (this.host instanceof TankListener listener) {
                listener.onChange(this);
            }
            this.host.setChanged();
        }
    }

    @Override
    public void setFluid(@NotNull FluidStack stack) {
        super.setFluid(stack);
        this.host.setChanged();
    }

}
