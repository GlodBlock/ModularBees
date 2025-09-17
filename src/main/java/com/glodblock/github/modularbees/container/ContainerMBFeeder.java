package com.glodblock.github.modularbees.container;

import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveFeeder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class ContainerMBFeeder extends ContainerMBBase<TileBeehiveFeeder> {

    public final static MBGuiHandler.MenuHandler TYPE = MBGuiHandler.builder(TileBeehiveFeeder.class)
            .factory(ContainerMBFeeder::new)
            .build("modular_beehive_feeder");

    protected ContainerMBFeeder(@Nullable MenuType<?> type, int id, Inventory inv, TileBeehiveFeeder host) {
        super(type, id, inv, host);
        this.addItemHandlerSlot(host.getHandlerByName("feeder"), 62, 17, 3);
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
