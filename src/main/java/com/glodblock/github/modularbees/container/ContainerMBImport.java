package com.glodblock.github.modularbees.container;

import com.glodblock.github.modularbees.common.tileentities.centrifuge.TileCentrifugeImport;
import com.glodblock.github.modularbees.container.base.ContainerMBBase;
import com.glodblock.github.modularbees.container.base.MBGuiHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class ContainerMBImport extends ContainerMBBase<TileCentrifugeImport> {

    public final static MBGuiHandler.MenuHandler TYPE = MBGuiHandler.builder(TileCentrifugeImport.class)
            .factory(ContainerMBImport::new)
            .build("modular_centrifuge_import");

    protected ContainerMBImport(@Nullable MenuType<?> type, int id, Inventory inv, TileCentrifugeImport host) {
        super(type, id, inv, host);
        this.addItemHandlerSlot(host.getHandlerByName("inputs"), 62, 17, 3);
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
