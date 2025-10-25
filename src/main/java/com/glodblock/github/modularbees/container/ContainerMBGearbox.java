package com.glodblock.github.modularbees.container;

import com.glodblock.github.modularbees.common.tileentities.centrifuge.TileCentrifugeGearbox;
import com.glodblock.github.modularbees.container.base.ContainerMBBase;
import com.glodblock.github.modularbees.container.base.MBGuiHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class ContainerMBGearbox extends ContainerMBBase<TileCentrifugeGearbox> {

    public final static MBGuiHandler.MenuHandler TYPE = MBGuiHandler.builder(TileCentrifugeGearbox.class)
            .factory(ContainerMBGearbox::new)
            .build("modular_centrifuge_gearbox");

    protected ContainerMBGearbox(@Nullable MenuType<?> type, int id, Inventory inv, TileCentrifugeGearbox host) {
        super(type, id, inv, host);
        this.addItemHandlerSlot(host.getHandlerByName("inv"), 80, 35, 1);
        this.bindPlayerInventorySlots(inv);
        this.getSync().addInt(1, this::getWax, wax -> this.getHost().setWax(wax));
    }

    public int getWax() {
        return this.getHost().getWax();
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