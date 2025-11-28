package com.glodblock.github.modularbees.container.base;

import com.glodblock.github.modularbees.common.tileentities.base.TileMBMachine;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public abstract class ContainerMBMachine<M extends TileMBMachine> extends ContainerMBBase<M> {

    protected ContainerMBMachine(@Nullable MenuType<?> type, int id, Inventory inv, M host) {
        super(type, id, inv, host);
        this.addInventorySlots();
        this.addItemHandlerSlot(host.getHandlerByName("upgrade"), 178, 8, 1);
        this.bindPlayerInventorySlots(inv);
        this.getSync()
                .addDouble(1, this::getProcess, this.getHost()::setProcess)
                .addInt(2, this::getEnergy, this.getHost().getEnergyStorage()::setStoredEnergy);
    }

    protected abstract void addInventorySlots();

    public int getEnergy() {
        return this.getHost().getEnergyStorage().getEnergyStored();
    }

    public double getProcess() {
        return this.getHost().getProcess();
    }

    @Override
    protected int getHeight() {
        return 166;
    }

    @Override
    protected int getWidth() {
        return 202;
    }

}
