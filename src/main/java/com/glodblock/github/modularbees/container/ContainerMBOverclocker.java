package com.glodblock.github.modularbees.container;

import com.glodblock.github.modularbees.common.tileentities.base.TileMBOverclocker;
import com.glodblock.github.modularbees.container.base.ContainerMBBase;
import com.glodblock.github.modularbees.container.base.MBGuiHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class ContainerMBOverclocker extends ContainerMBBase<TileMBOverclocker> {

    public final static MBGuiHandler.MenuHandler TYPE = MBGuiHandler.builder(TileMBOverclocker.class)
            .factory(ContainerMBOverclocker::new)
            .build("modular_beehive_overclocker");

    protected ContainerMBOverclocker(@Nullable MenuType<?> type, int id, Inventory inv, TileMBOverclocker host) {
        super(type, id, inv, host);
        this.addItemHandlerSlot(host.getHandlerByName("electrode"), 80, 35, 1);
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
