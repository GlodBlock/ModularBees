package com.glodblock.github.modularbees.common.inventory;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.energy.EnergyStorage;
import org.jetbrains.annotations.NotNull;

public class MBEnergyInventory extends EnergyStorage {

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
    public int receiveEnergy(int toReceive, boolean simulate) {
        if (!this.mode.canInsert()) {
            return 0;
        }
        int accepted = super.receiveEnergy(toReceive, simulate);
        if (this.host != null && accepted > 0 && !simulate) {
            this.host.setChanged();
        }
        return accepted;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        if (!this.mode.canExtract()) {
            return 0;
        }
        int extracted = super.extractEnergy(toExtract, simulate);
        if (this.host != null && extracted > 0 && !simulate) {
            this.host.setChanged();
        }
        return extracted;
    }

    public void setStoredEnergy(int energy) {
        if (energy != this.getEnergyStored()) {
            this.energy = energy;
            if (this.host != null) {
                this.host.setChanged();
            }
        }
    }

    public int forceInsertEnergy(int toReceive, boolean simulate) {
        return super.receiveEnergy(toReceive, simulate);
    }

    public int forceExtractEnergy(int toExtract, boolean simulate) {
        return super.extractEnergy(toExtract, simulate);
    }

    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        var tag = new CompoundTag();
        tag.putInt("energy", this.energy);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull Tag nbt) {
        if (nbt instanceof CompoundTag tag) {
            this.energy = tag.getInt("energy");
        }
    }

}
