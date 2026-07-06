package com.glodblock.github.modularbees.common.caps;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

public interface EnergyHandlerHost {

    default EnergyHandler getEnergyStorage(Direction side) {
        return this.getEnergyStorage();
    }

    EnergyHandler getEnergyStorage();

}
