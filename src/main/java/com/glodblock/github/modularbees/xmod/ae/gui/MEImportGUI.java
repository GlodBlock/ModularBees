package com.glodblock.github.modularbees.xmod.ae.gui;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.gui.MBBaseGui;
import com.glodblock.github.modularbees.client.util.PicData;
import com.glodblock.github.modularbees.xmod.ae.container.ContainerMEImport;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class MEImportGUI extends MBBaseGui<ContainerMEImport> {

    private static final PicData BG = PicData.of(ModularBees.id("textures/gui/me_import.png")).select(0, 0, 176, 176);
    private static final PicData SLOT_HINT = PicData.of(ModularBees.id("textures/gui/me_import.png")).select(176, 0, 16, 16);

    public MEImportGUI(ContainerMEImport container, Inventory inv, Component component) {
        super(container, inv, component);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected PicData getBackground() {
        return BG;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);
        var storage = this.getMenu().getHost().getConfig();
        for (var slot = 0; slot < storage.getSlots(); slot++) {
            if (storage.getStackInSlot(slot).isEmpty()) {
                int x = slot % 3;
                int y = slot / 3;
                SLOT_HINT.render(graphics, 26 + x * 18 + this.leftPos, 26 + y * 18 + this.topPos);
            }
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        graphics.drawString(this.font, Component.translatable("modularbees.gui.me_import.config"),25, 15, DEFAULT_TEXT_COLOR, false);
        graphics.drawString(this.font, Component.translatable("modularbees.gui.me_import.storage"),97, 15, DEFAULT_TEXT_COLOR, false);
    }

    @Override
    protected Component getGuiName() {
        return this.menu.getHost().getDisplayName();
    }

}
