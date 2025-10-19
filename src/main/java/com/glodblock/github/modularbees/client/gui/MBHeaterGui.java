package com.glodblock.github.modularbees.client.gui;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.gui.elements.EnergyDisplay;
import com.glodblock.github.modularbees.client.util.PicData;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.container.ContainerMBHeater;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MBHeaterGui extends MBBaseGui<ContainerMBHeater> {

    private static final PicData BG = PicData.of(ModularBees.id("textures/gui/heater.png")).select(0, 0, 176, 166);

    public MBHeaterGui(ContainerMBHeater container, Inventory inv, Component component) {
        super(container, inv, component);
        var energy = new EnergyDisplay(container::getEnergy)
                .capacity(container.getHost().getEnergyStorage().getMaxEnergyStored())
                .texture(PicData.of(ModularBees.id("textures/gui/heater.png")).select(176, 0, 11, 52));
        energy.setPosition(82, 17);
        energy.setSize(11, 52);
        this.group.add("energy", energy);
    }

    @Override
    protected PicData getBackground() {
        return BG;
    }

    @Override
    protected Component getGuiName() {
        return MBSingletons.MODULAR_CENTRIFUGE_HEATER.getName();
    }

}
