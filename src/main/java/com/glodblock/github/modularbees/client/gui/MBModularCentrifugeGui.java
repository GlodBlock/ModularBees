package com.glodblock.github.modularbees.client.gui;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.gui.elements.FluidTankDisplay;
import com.glodblock.github.modularbees.client.gui.elements.ProcessDisplay;
import com.glodblock.github.modularbees.client.util.PicData;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.tileentities.centrifuge.TileModularCentrifuge;
import com.glodblock.github.modularbees.container.ContainerMBModularCentrifuge;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MBModularCentrifugeGui extends MBBaseGui<ContainerMBModularCentrifuge> {

    private static final PicData BG = PicData.of(ModularBees.id("textures/gui/modular_centrifuge_core.png")).select(0, 0, 202, 166);

    public MBModularCentrifugeGui(ContainerMBModularCentrifuge container, Inventory inv, Component component) {
        super(container, inv, component);
        for (int x = 0; x < TileModularCentrifuge.FLUID_TANKS; x ++) {
            final int slot = x;
            var tank = new FluidTankDisplay(() -> container.getTank(slot)).capacity(container.getHost().getFluidInventory().getTankCapacity(slot));
            tank.setPosition(127 + slot * 13, 17);
            tank.setSize(11, 52);
            this.group.add("output_tank_" + slot, tank);
        }
        var process = new ProcessDisplay(container::getProcess)
                .full(TileModularCentrifuge.WAITING_TICKS)
                .texture(PicData.of(ModularBees.id("textures/gui/modular_centrifuge_core.png")).select(202, 0, 16, 12));
        process.setPosition(41, 35);
        process.setSize(16, 12);
        this.group.add("process", process);
    }

    @Override
    protected Component getGuiName() {
        return MBSingletons.MODULAR_CENTRIFUGE_CORE.getName();
    }

    @Override
    protected PicData getBackground() {
        return BG;
    }

}
