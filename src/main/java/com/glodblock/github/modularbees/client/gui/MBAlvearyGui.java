package com.glodblock.github.modularbees.client.gui;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.gui.elements.BeeDisplay;
import com.glodblock.github.modularbees.client.util.PicData;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.container.ContainerMBAlveary;
import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBee;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class MBAlvearyGui extends MBBaseGui<ContainerMBAlveary> {

    private static final PicData BG = PicData.of(ModularBees.id("textures/gui/alveary.png")).select(0, 0, 176, 166);
    private static final PicData INPUT = PicData.of(ModularBees.id("textures/gui/alveary.png")).select(176, 0, 16, 16);
    private static final PicData OUTPUT = PicData.of(ModularBees.id("textures/gui/alveary.png")).select(176, 16, 16, 16);

    public MBAlvearyGui(ContainerMBAlveary container, Inventory inv, Component component) {
        super(container, inv, component);
        this.group.add("bee_0", this.createBee(0, 61, 25));
        this.group.add("bee_1", this.createBee(1, 61, 46));
        this.group.add("bee_2", this.createBee(2, 79, 36));
        this.group.add("bee_3", this.createBee(3, 97, 25));
        this.group.add("bee_4", this.createBee(4, 97, 46));
    }

    private BeeDisplay createBee(int index, int x, int y) {
        var display = new BeeDisplay(() -> this.getMenu().getBee(index));
        display.setSize(16, 16);
        display.setPosition(x, y);
        return display;
    }

    @Override
    protected Component getGuiName() {
        return MBSingletons.MODULAR_ALVEARY.getName();
    }

    @Override
    PicData getBackground() {
        return BG;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);
        if (this.getMenu().renderInputSlot()) {
            INPUT.render(graphics, 32 + this.leftPos, 36 + this.topPos);
        }
        if (this.getMenu().renderOutputSlot()) {
            OUTPUT.render(graphics, 129 + this.leftPos, 36 + this.topPos);
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        var inputBee = this.getMenu().getInputBee();
        if (inputBee != null) {
            if (inputBee instanceof SolitaryBee) {
                this.drawStringCenter(graphics, Component.translatable("modularbees.gui.modular_beehive_alveary.solitary").withStyle(ChatFormatting.RED), 87, -9, true);
            } else if (inputBee.getAge() < 0) {
                this.drawStringCenter(graphics, Component.translatable("modularbees.gui.modular_beehive_alveary.child").withStyle(ChatFormatting.RED), 87, -9, true);
            }
        }
    }

}
