package com.glodblock.github.modularbees.client.gui;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.gui.elements.FluidTankDisplay;
import com.glodblock.github.modularbees.client.util.PicData;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.container.ContainerMBModularBeehive;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MBModularBeehiveGui extends MBBaseGui<ContainerMBModularBeehive> {

    private static final PicData BG = PicData.of(ModularBees.id("textures/gui/modular_beehive_core.png")).select(0, 0, 202, 166);

    public MBModularBeehiveGui(ContainerMBModularBeehive container, Inventory inv, Component component) {
        super(container, inv, component);
        var tank = new FluidTankDisplay(container::getHoney).capacity(container.getHost().getFluidInventory().getCapacity());
        tank.setPosition(14, 17);
        tank.setSize(11, 52);
        this.group.add("honey", tank);
    }

    @Override
    protected Component getGuiName() {
        return MBSingletons.MODULAR_BEEHIVE_CORE.getName();
    }

    @Override
    protected PicData getBackground() {
        return BG;
    }

}
