package com.glodblock.github.modularbees.container;

import com.glodblock.github.modularbees.common.tileentities.centrifuge.TileCentrifugeHeater;
import com.glodblock.github.modularbees.container.base.ContainerMBBase;
import com.glodblock.github.modularbees.container.base.MBGuiHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class ContainerMBHeater extends ContainerMBBase<TileCentrifugeHeater> {

    public final static MBGuiHandler.MenuHandler TYPE = MBGuiHandler.builder(TileCentrifugeHeater.class)
            .factory(ContainerMBHeater::new)
            .build("modular_centrifuge_heater");

    protected ContainerMBHeater(@Nullable MenuType<?> type, int id, Inventory inv, TileCentrifugeHeater host) {
        super(type, id, inv, host);
        this.bindPlayerInventorySlots(inv);
        this.getSync().addInt(1, this::getEnergy, energy -> this.getHost().getEnergyStorage().setStoredEnergy(energy));
    }

    public int getEnergy() {
        return this.getHost().getEnergyStorage().getEnergyStored();
    }

    @Override
    protected int getHeight() {
        return 166;
    }

    @Override
    protected int getWidth() {
        return 176;
    }

}
