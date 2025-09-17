package com.glodblock.github.modularbees.client.gui;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.util.PicData;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.container.ContainerMBTreater;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MBTreaterGui extends MBBaseGui<ContainerMBTreater> {

    private static final PicData BG = PicData.of(ModularBees.id("textures/gui/treater.png")).select(0, 0, 176, 166);

    public MBTreaterGui(ContainerMBTreater container, Inventory inv, Component component) {
        super(container, inv, component);
    }

    @Override
    PicData getBackground() {
        return BG;
    }

    @Override
    protected Component getGuiName() {
        return MBSingletons.MODULAR_TREATER.getName();
    }

}