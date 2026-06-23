package com.glodblock.github.modularbees.container;

import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveFeeder;
import com.glodblock.github.modularbees.container.base.ContainerMBBase;
import com.glodblock.github.modularbees.container.base.MBGuiHandler;
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
    protected int getHeight() {
        return 166;
    }

    @Override
    protected int getWidth() {
        return 176;
    }

}
