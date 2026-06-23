package com.glodblock.github.modularbees.client.gui;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.util.PicData;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.container.ContainerMBImport;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MBImportGui extends MBBaseGui<ContainerMBImport> {

    private static final PicData BG = PicData.of(ModularBees.id("textures/gui/import.png")).select(0, 0, 176, 166);

    public MBImportGui(ContainerMBImport container, Inventory inv, Component component) {
        super(container, inv, component);
    }

    @Override
    protected PicData getBackground() {
        return BG;
    }

    @Override
    protected Component getGuiName() {
        return MBSingletons.MODULAR_CENTRIFUGE_IMPORT.getName();
    }

}
