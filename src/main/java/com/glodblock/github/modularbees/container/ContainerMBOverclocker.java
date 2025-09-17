package com.glodblock.github.modularbees.container;

import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveOverclocker;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class ContainerMBOverclocker extends ContainerMBBase<TileBeehiveOverclocker> {

    public final static MBGuiHandler.MenuHandler TYPE = MBGuiHandler.builder(TileBeehiveOverclocker.class)
            .factory(ContainerMBOverclocker::new)
            .build("modular_beehive_overclocker");

    protected ContainerMBOverclocker(@Nullable MenuType<?> type, int id, Inventory inv, TileBeehiveOverclocker host) {
        super(type, id, inv, host);
        this.addItemHandlerSlot(host.getHandlerByName("electrode"), 80, 35, 1);
        this.bindPlayerInventorySlots(inv);
        this.getSync().addInt(1, this::getEnergy, energy -> this.getHost().getEnergyStorage().setStoredEnergy(energy));
    }

    public int getEnergy() {
        return this.getHost().getEnergyStorage().getEnergyStored();
    }

    @Override
    int getHeight() {
        return 166;
    }

    @Override
    int getWidth() {
        return 176;
    }

}
