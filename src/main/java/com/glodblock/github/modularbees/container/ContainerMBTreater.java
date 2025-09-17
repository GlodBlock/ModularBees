package com.glodblock.github.modularbees.container;

import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveTreater;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class ContainerMBTreater extends ContainerMBBase<TileBeehiveTreater> {

    public final static MBGuiHandler.MenuHandler TYPE = MBGuiHandler.builder(TileBeehiveTreater.class)
            .factory(ContainerMBTreater::new)
            .build("modular_beehive_treater");

    protected ContainerMBTreater(@Nullable MenuType<?> type, int id, Inventory inv, TileBeehiveTreater host) {
        super(type, id, inv, host);
        this.addItemHandlerSlot(host.getHandlerByName("foods"), 62, 27, 3);
        this.bindPlayerInventorySlots(inv);
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
