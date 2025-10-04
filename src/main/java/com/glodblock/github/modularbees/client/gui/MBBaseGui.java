package com.glodblock.github.modularbees.client.gui;

import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.modularbees.client.util.ElementGroup;
import com.glodblock.github.modularbees.client.util.PicData;
import com.glodblock.github.modularbees.client.util.TooltipElement;
import com.glodblock.github.modularbees.common.tileentities.base.TileMBBase;
import com.glodblock.github.modularbees.container.base.ContainerMBBase;
import com.glodblock.github.modularbees.network.CMBGenericPacket;
import com.glodblock.github.modularbees.network.MBNetworkHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class MBBaseGui<C extends ContainerMBBase<? extends TileMBBase>> extends AbstractContainerScreen<C> implements IActionHolder {

    public static final int DEFAULT_TEXT_COLOR = 4210752;
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
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.updateGuiData();
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
        for (var e : this.group) {
            if (e instanceof TooltipElement tooltip) {
                if (tooltip.shouldDisplay(mouseX, mouseY) && !tooltip.getTooltipMessage(hasShiftDown()).isEmpty()) {
                    var lines = tooltip.getTooltipMessage(hasShiftDown());
                    int maxWidth = width / 2 - 40;
                    List<FormattedCharSequence> styledLines = new ArrayList<>(lines.size());
                    for (Component line : lines) {
                        styledLines.addAll(ComponentRenderUtils.wrapComponents(line, maxWidth, font));
                    }
                    graphics.renderTooltip(this.font, styledLines, mouseX, mouseY);
                    break;
                }
            }
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        // Background rendering doesn't count GUI offset yet
        this.getBackground().render(graphics, this.leftPos, this.topPos);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        graphics.drawString(this.font, this.getGuiName(), this.titleLabelX, this.titleLabelY, DEFAULT_TEXT_COLOR, false);
    }

    protected void drawStringCenter(@NotNull GuiGraphics graphics, Component text, int centerX, int y) {
        this.drawStringCenter(graphics, text, centerX, y, false);
    }

    protected void drawStringCenter(@NotNull GuiGraphics graphics, Component text, int centerX, int y, boolean shadow) {
        var length = this.font.width(text);
        graphics.drawString(
                this.font, text,
                centerX - length / 2, y,
                DEFAULT_TEXT_COLOR, shadow
        );
    }

    protected void drawStringCenter(@NotNull GuiGraphics graphics, String text, int centerX, int y) {
        var length = this.font.width(text);
        graphics.drawString(
                this.font, text,
                centerX - length / 2, y,
                DEFAULT_TEXT_COLOR, false
        );
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }

}
