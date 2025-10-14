package com.glodblock.github.modularbees.container;

import com.glodblock.github.modularbees.common.tileentities.centrifuge.TileModularCentrifuge;
import com.glodblock.github.modularbees.container.base.ContainerMBBase;
import com.glodblock.github.modularbees.container.base.MBGuiHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class ContainerMBModularCentrifuge extends ContainerMBBase<TileModularCentrifuge> {

    public final static MBGuiHandler.MenuHandler TYPE = MBGuiHandler.builder(TileModularCentrifuge.class)
            .factory(ContainerMBModularCentrifuge::new)
            .build("modular_centrifuge_core");

    protected ContainerMBModularCentrifuge(@Nullable MenuType<?> type, int id, Inventory inv, TileModularCentrifuge host) {
        super(type, id, inv, host);
        this.addItemHandlerSlot(host.getHandlerByName("upgrade"), 178, 8, 1);
        this.addItemHandlerSlot(host.getHandlerByName("inputs"), 21, 17, 1);
        this.addItemHandlerSlot(host.getHandlerByName("outputs"), 61, 17, 3);
        this.bindPlayerInventorySlots(inv);
        this.getSync()
                .addDouble(1, this::getProcess, this.getHost()::setProcess)
                .addFluid(2, () -> this.getTank(0), fluid -> this.getHost().setTankFluid(0, fluid))
                .addFluid(3, () -> this.getTank(1), fluid -> this.getHost().setTankFluid(1, fluid))
                .addFluid(4, () -> this.getTank(2), fluid -> this.getHost().setTankFluid(2, fluid));
    }

    public FluidStack getTank(int slot) {
        return this.getHost().getFluidInventory().getFluidInTank(slot);
    }

    public double getProcess() {
        return this.getHost().getProcess();
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
