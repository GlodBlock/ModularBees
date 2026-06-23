package com.glodblock.github.modularbees.client.gui;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.gui.elements.EnergyDisplay;
import com.glodblock.github.modularbees.client.gui.elements.ProcessDisplay;
import com.glodblock.github.modularbees.client.util.PicData;
import com.glodblock.github.modularbees.common.MBConfig;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.container.ContainerMBBeeExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MBBeeExtractorGui extends MBBaseGui<ContainerMBBeeExtractor> {

    private static final PicData BG = PicData.of(ModularBees.id("textures/gui/bee_extractor.png")).select(0, 0, 202, 166);

    public MBBeeExtractorGui(ContainerMBBeeExtractor container, Inventory inv, Component component) {
        super(container, inv, component);
        var process = new ProcessDisplay(container::getProcess)
                .full(MBConfig.BEE_EXTRACTOR_TIME.get())
                .texture(PicData.of(ModularBees.id("textures/gui/bee_extractor.png")).select(202, 52, 16, 12));
        process.setPosition(88, 37);
        process.setSize(16, 12);
        var energy = new EnergyDisplay(container::getEnergy)
                .capacity(container.getHost().getEnergyStorage().getMaxEnergyStored())
                .texture(PicData.of(ModularBees.id("textures/gui/bee_extractor.png")).select(202, 0, 11, 52));
        energy.setPosition(20, 17);
        energy.setSize(11, 52);
        this.group.add("energy", energy);
        this.group.add("process", process);
    }

    @Override
    protected Component getGuiName() {
        return MBSingletons.BEE_EXTRACTOR.getName();
    }

    @Override
    protected PicData getBackground() {
        return BG;
    }

}
