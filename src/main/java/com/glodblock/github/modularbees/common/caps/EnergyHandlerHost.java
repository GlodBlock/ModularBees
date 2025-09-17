package com.glodblock.github.modularbees.common.caps;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;

public interface EnergyHandlerHost {

    default IEnergyStorage getEnergyStorage(Direction side) {
        return this.getEnergyStorage();
    }

    IEnergyStorage getEnergyStorage();

}
