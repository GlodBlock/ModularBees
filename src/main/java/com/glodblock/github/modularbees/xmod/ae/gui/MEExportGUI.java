package com.glodblock.github.modularbees.xmod.ae.gui;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.gui.MBBaseGui;
import com.glodblock.github.modularbees.client.gui.elements.IconButton;
import com.glodblock.github.modularbees.client.util.PicData;
import com.glodblock.github.modularbees.xmod.ae.container.ContainerMEExport;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class MEExportGUI extends MBBaseGui<ContainerMEExport> {

    private static final PicData BG = PicData.of(ModularBees.id("textures/gui/me_export.png")).select(0, 0, 176, 166);
    private static final PicData BUTTON_BG = PicData.of(ModularBees.id("textures/gui/me_export.png")).select(176, 56, 14, 14);
    private static final PicData BUTTON_BG_H = PicData.of(ModularBees.id("textures/gui/me_export.png")).select(176, 70, 14, 14);

    private final IconButton enableButton = new IconButton();
    private final IconButton modeButton = new IconButton();

    public MEExportGUI(ContainerMEExport container, Inventory inv, Component component) {
        super(container, inv, component);
        this.enableButton.setPosition(130, 22);
        this.enableButton.setSize(14, 14);
        this.enableButton.setBackground(BUTTON_BG);
        this.enableButton.setBackgroundHighlight(BUTTON_BG_H);
        this.enableButton.addAction(
                PicData.of(ModularBees.id("textures/gui/me_export.png")).select(176, 0, 14, 14),
                i -> this.sendAction("enable_filter", true),
                List.of(Component.translatable("modularbees.gui.me_export.filter_disable"))
        );
        this.enableButton.addAction(
                PicData.of(ModularBees.id("textures/gui/me_export.png")).select(176, 14, 14, 14),
                i -> this.sendAction("enable_filter", false),
                List.of(Component.translatable("modularbees.gui.me_export.filter_enable"))
        );
        this.modeButton.setPosition(130, 50);
        this.modeButton.setSize(14, 14);
        this.modeButton.setBackground(BUTTON_BG);
        this.modeButton.setBackgroundHighlight(BUTTON_BG_H);
        this.modeButton.addAction(
                PicData.of(ModularBees.id("textures/gui/me_export.png")).select(176, 28, 14, 14),
                i -> this.sendAction("filter_mode", false),
                List.of(Component.translatable("modularbees.gui.me_export.whitelist"))
        );
        this.modeButton.addAction(
                PicData.of(ModularBees.id("textures/gui/me_export.png")).select(176, 42, 14, 14),
                i -> this.sendAction("filter_mode", true),
                List.of(Component.translatable("modularbees.gui.me_export.blacklist"))
        );
        this.group.add("enable_button", this.enableButton);
        this.group.add("mode_button", this.modeButton);
    }

    @Override
    protected void updateGuiData() {
        this.enableButton.setStatus(this.getMenu().isEnableFilter() ? 1 : 0);
        this.modeButton.setStatus(this.getMenu().isWhitelist() ? 0 : 1);
    }

    @Override
    protected PicData getBackground() {
        return BG;
    }

    @Override
    protected Component getGuiName() {
        return this.menu.getHost().getDisplayName();
    }

}