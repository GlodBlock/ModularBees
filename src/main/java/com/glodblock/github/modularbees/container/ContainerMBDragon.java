package com.glodblock.github.modularbees.container;

import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveDragon;
import com.glodblock.github.modularbees.container.base.ContainerMBBase;
import com.glodblock.github.modularbees.container.base.MBGuiHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jetbrains.annotations.Nullable;

public class ContainerMBDragon extends ContainerMBBase<TileBeehiveDragon> {

    public final static MBGuiHandler.MenuHandler TYPE = MBGuiHandler.builder(TileBeehiveDragon.class)
            .factory(ContainerMBDragon::new)
            .build("modular_dragon_hive");

    protected ContainerMBDragon(@Nullable MenuType<?> type, int id, Inventory inv, TileBeehiveDragon host) {
        super(type, id, inv, host);
        this.addSlot(host.getHandlerByName("bottle"), 0, 90, 17);
        this.addSlot(host.getHandlerByName("bottle"), 1, 90, 53);
        this.bindPlayerInventorySlots(inv);
        this.getSync().addFluid(1, this::getDragonBreath, fluid -> host.getFluidInventory().set(0, FluidResource.of(fluid), fluid.getAmount()));
    }

    public FluidStack getDragonBreath() {
        return this.getHost().getFluidInventory().getFluidStack(0);
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
