package com.glodblock.github.modularbees.common.inventory;

import com.glodblock.github.modularbees.common.tileentities.base.TileMBBase;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

public class MBEnergyInventory extends SimpleEnergyHandler {

    protected IO mode = IO.ALL;
    protected final BlockEntity host;

    public MBEnergyInventory(BlockEntity host, int capacity) {
        super(capacity);
        this.host = host;
    }

    public MBEnergyInventory inputOnly() {
        this.mode = IO.IN;
        return this;
    }

    public MBEnergyInventory outputOnly() {
        this.mode = IO.OUT;
        return this;
    }

    @Override
    public int insert(int amount, @NotNull TransactionContext transaction) {
        if (!this.mode.canInsert()) {
            return 0;
        }
        return super.insert(amount, transaction);
    }

    @Override
    public int extract(int toExtract, @NotNull TransactionContext transaction) {
        if (!this.mode.canExtract()) {
            return 0;
        }
        return super.extract(toExtract, transaction);
    }

    public void setStoredEnergy(int energy) {
        if (energy != this.getAmountAsInt()) {
            this.energy = energy;
            if (this.host != null) {
                this.markDirty();
            }
        }
    }

    public int forceInsert(int toReceive, @NotNull TransactionContext transaction) {
        return super.insert(toReceive, transaction);
    }

    public int forceExtract(int toExtract, @NotNull TransactionContext transaction) {
        return super.extract(toExtract, transaction);
    }

    private void markDirty() {
        if (this.host instanceof TileMBBase base) {
            base.markDirty();
        } else {
            this.host.setChanged();
        }
    }

}
