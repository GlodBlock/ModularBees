package com.glodblock.github.modularbees.container;

import com.glodblock.github.modularbees.common.tileentities.misc.TileBeeExtractor;
import com.glodblock.github.modularbees.container.base.ContainerMBMachine;
import com.glodblock.github.modularbees.container.base.MBGuiHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class ContainerMBBeeExtractor extends ContainerMBMachine<TileBeeExtractor> {

    public final static MBGuiHandler.MenuHandler TYPE = MBGuiHandler.builder(TileBeeExtractor.class)
            .factory(ContainerMBBeeExtractor::new)
            .build("bee_extractor");

    protected ContainerMBBeeExtractor(@Nullable MenuType<?> type, int id, Inventory inv, TileBeeExtractor host) {
        super(type, id, inv, host);
    }

    @Override
    protected void addInventorySlots() {
        this.addItemHandlerSlot(this.getHost().getHandlerByName("outputs"), 107, 35, 2);
        this.addItemHandlerSlot(this.getHost().getHandlerByName("inputs"), 51, 35, 2);
    }

}
