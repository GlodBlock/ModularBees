package com.glodblock.github.modularbees.container;

import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveDragon;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.fluids.FluidStack;
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
        this.getSync().addFluid(1, this::getDragonBreath, fluid -> host.getFluidInventory().setFluid(fluid));
    }

    public FluidStack getDragonBreath() {
        return this.getHost().getFluidInventory().getFluid();
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
