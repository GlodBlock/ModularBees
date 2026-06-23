package com.glodblock.github.modularbees.container;

import com.glodblock.github.modularbees.common.tileentities.hive.TileModularBeehive;
import com.glodblock.github.modularbees.container.base.ContainerMBBase;
import com.glodblock.github.modularbees.container.base.MBGuiHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class ContainerMBModularBeehive extends ContainerMBBase<TileModularBeehive> {

    public final static MBGuiHandler.MenuHandler TYPE = MBGuiHandler.builder(TileModularBeehive.class)
            .factory(ContainerMBModularBeehive::new)
            .build("modular_beehive_core");

    protected ContainerMBModularBeehive(@Nullable MenuType<?> type, int id, Inventory inv, TileModularBeehive host) {
        super(type, id, inv, host);
        this.addItemHandlerSlot(host.getHandlerByName("outputs"), 62, 17, 6);
        this.addSlot(host.getHandlerByName("bottle"), 0, 35, 17);
        this.addSlot(host.getHandlerByName("bottle"), 1, 35, 53);
        this.addItemHandlerSlot(host.getHandlerByName("upgrade"), 178, 8, 1);
        this.bindPlayerInventorySlots(inv);
        this.getSync().addFluid(1, this::getHoney, fluid -> host.getFluidInventory().setFluid(fluid));
    }

    public FluidStack getHoney() {
        return this.getHost().getFluidInventory().getFluid();
    }

    @Override
    protected int getHeight() {
        return 166;
    }

    @Override
    protected int getWidth() {
        return 202;
    }

    @Override
    public void broadcastChanges() {
        if (!this.getHost().isFormed()) {
            this.invalidate();
            return;
        }
        super.broadcastChanges();
    }

}
