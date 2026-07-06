package com.glodblock.github.modularbees.common.inventory;

import com.glodblock.github.modularbees.common.tileentities.base.TileMBBase;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class MBFluidInventory extends FluidStacksResourceHandler {

    protected IO mode = IO.ALL;
    protected final BlockEntity host;
    protected Predicate<FluidResource> filter = _ -> true;

    public MBFluidInventory(BlockEntity host, int capacity) {
        super(1, capacity);
        this.host = host;
    }

    public MBFluidInventory(BlockEntity host, int size, int capacity) {
        super(size, capacity);
        this.host = host;
    }

    public MBFluidInventory(BlockEntity host, int capacity, Predicate<FluidResource> validator) {
        super(1, capacity);
        this.host = host;
        this.filter = validator;
    }

    public MBFluidInventory inputOnly() {
        this.mode = IO.IN;
        return this;
    }

    public MBFluidInventory outputOnly() {
        this.mode = IO.OUT;
        return this;
    }

    public int forceInsert(int slot, @NotNull FluidResource resource, int amount, @NotNull TransactionContext transaction) {
        return super.insert(slot, resource, amount, transaction);
    }

    public int forceInsert(@NotNull FluidResource resource, int amount, @NotNull TransactionContext transaction) {
        return super.insert(resource, amount, transaction);
    }

    public int forceExtract(int slot, @NotNull FluidResource resource, int amount, @NotNull TransactionContext transaction) {
        return super.extract(slot, resource, amount, transaction);
    }

    public int forceExtract(@NotNull FluidResource resource, int amount, @NotNull TransactionContext transaction) {
        return super.extract(resource, amount, transaction);
    }

    @Override
    public int insert(int slot, @NotNull FluidResource resource, int amount, @NotNull TransactionContext transaction) {
        if (this.mode.canInsert() && this.filter.test(resource)) {
            return super.insert(slot, resource, amount, transaction);
        } else {
            return 0;
        }
    }

    @Override
    public int extract(int slot, @NotNull FluidResource resource, int amount, @NotNull TransactionContext transaction) {
        if (this.mode.canExtract() ) {
            return super.extract(slot, resource, amount, transaction);
        } else {
            return 0;
        }
    }

    @Override
    protected void onContentsChanged(int slot, FluidStack stack) {
        if (this.host != null) {
            if (this.host instanceof TankListener listener) {
                listener.onChange(this);
            }
            this.markDirty();
        }
    }

    public int getCapacity() {
        return this.capacity;
    }

    public FluidStack getFluidStack(int slot) {
        return this.stacks.get(slot);
    }

    private void markDirty() {
        if (this.host instanceof TileMBBase base) {
            base.markDirty();
        } else {
            this.host.setChanged();
        }
    }

}
