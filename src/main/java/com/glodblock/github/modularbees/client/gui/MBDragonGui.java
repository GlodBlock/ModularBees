package com.glodblock.github.modularbees.client.gui;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.gui.elements.FluidTankDisplay;
import com.glodblock.github.modularbees.client.util.PicData;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.container.ContainerMBDragon;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MBDragonGui extends MBBaseGui<ContainerMBDragon> {

    private static final PicData BG = PicData.of(ModularBees.id("textures/gui/dragon_hive.png")).select(0, 0, 176, 166);

    public MBDragonGui(ContainerMBDragon container, Inventory inv, Component component) {
        super(container, inv, component);
        var tank = new FluidTankDisplay(container::getDragonBreath).capacity(container.getHost().getFluidInventory().getCapacity());
        tank.setPosition(69, 17);
        tank.setSize(11, 52);
        this.group.add("dragon_breath", tank);
    }

    @Override
    protected Component getGuiName() {
        return MBSingletons.MODULAR_DRAGON_HIVE.getName();
    }

    @Override
    protected PicData getBackground() {
        return BG;
    }

}