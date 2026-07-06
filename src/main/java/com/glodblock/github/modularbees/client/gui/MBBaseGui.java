package com.glodblock.github.modularbees.client.gui;

import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.modularbees.client.util.ElementGroup;
import com.glodblock.github.modularbees.client.util.PicData;
import com.glodblock.github.modularbees.client.util.TooltipElement;
import com.glodblock.github.modularbees.common.tileentities.base.TileMBBase;
import com.glodblock.github.modularbees.container.base.ContainerMBBase;
import com.glodblock.github.modularbees.container.slot.DisplaySlot;
import com.glodblock.github.modularbees.network.CMBGenericPacket;
import com.glodblock.github.modularbees.network.MBNetworkHandler;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class MBBaseGui<C extends ContainerMBBase<? extends TileMBBase>> extends AbstractContainerScreen<@NotNull C> implements IActionHolder {

    public static final int DEFAULT_TEXT_COLOR = -12566464;
    protected final ElementGroup group = new ElementGroup();
    private final ActionMap actions = ActionMap.create();

    public MBBaseGui(C container, Inventory inv, Component component) {
        super(container, inv, component);
    }

    protected abstract PicData getBackground();

    protected Component getGuiName() {
        return Component.empty();
    }

    protected void updateGuiData() {

    }

    public void sendAction(String id, Object... paras) {
        MBNetworkHandler.INSTANCE.sendToServer(new CMBGenericPacket(id, paras));
    }

    @Override
    protected void init() {
        var select = this.getBackground().getSelect();
        this.imageHeight = select.getHeight();
        this.imageWidth = select.getWidth();
        super.init();
        this.group.reposition(this.leftPos, this.topPos);
        this.group.populate(this::addRenderableOnly, this::addRenderableWidget);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        this.updateGuiData();
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        this.extractTooltip(graphics, mouseX, mouseY);
        for (var e : this.group) {
            if (e instanceof TooltipElement tooltip) {
                if (tooltip.shouldDisplay(mouseX, mouseY) && !tooltip.getTooltipMessage(hasShiftDown()).isEmpty()) {
                    var lines = tooltip.getTooltipMessage(hasShiftDown());
                    int maxWidth = width / 2 - 40;
                    List<FormattedCharSequence> styledLines = new ArrayList<>(lines.size());
                    for (Component line : lines) {
                        styledLines.addAll(ComponentRenderUtils.wrapComponents(line, maxWidth, font));
                    }
                    graphics.setTooltipForNextFrame(this.font, styledLines, mouseX, mouseY);
                    break;
                }
            }
        }
    }

    @Override
    protected void extractMenuBackground(@NotNull GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        // Background rendering doesn't count GUI offset yet
        this.getBackground().render(graphics, this.leftPos, this.topPos);
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        graphics.text(this.font, this.getGuiName(), this.titleLabelX, this.titleLabelY, DEFAULT_TEXT_COLOR, false);
    }

    protected void drawStringCenter(@NotNull GuiGraphicsExtractor graphics, Component text, int centerX, int y) {
        this.drawStringCenter(graphics, text, centerX, y, false);
    }

    protected void drawStringCenter(@NotNull GuiGraphicsExtractor graphics, Component text, int centerX, int y, boolean shadow) {
        var length = this.font.width(text);
        graphics.text(
                this.font, text,
                centerX - length / 2, y,
                DEFAULT_TEXT_COLOR, shadow
        );
    }

    protected void drawStringCenter(@NotNull GuiGraphicsExtractor graphics, String text, int centerX, int y) {
        var length = this.font.width(text);
        graphics.text(
                this.font, text,
                centerX - length / 2, y,
                DEFAULT_TEXT_COLOR, false
        );
    }

    @Override
    protected void slotClicked(@NotNull Slot slot, int slotIdx, int mouseButton, @NotNull ContainerInput input) {
        if (slot instanceof DisplaySlot) {
            this.sendAction("display_slot_click", slot.index);
        } else {
            super.slotClicked(slot, slotIdx, mouseButton, input);
        }
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }

    public boolean hasShiftDown() {
        return this.getMinecraft().hasShiftDown();
    }

}
