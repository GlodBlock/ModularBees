package com.glodblock.github.modularbees.client.gui.elements;

import com.glodblock.github.modularbees.client.util.PicData;
import com.glodblock.github.modularbees.client.util.RelativePosition;
import com.glodblock.github.modularbees.client.util.Resizable;
import com.glodblock.github.modularbees.client.util.TooltipElement;
import com.glodblock.github.modularbees.util.GameUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class IconButton extends Button implements RelativePosition, Resizable, TooltipElement {

    private final List<ActionPair> actions = new ArrayList<>();
    private PicData background;
    private PicData backgroundHighlight;
    private int index = 0;
    private int offsetX;
    private int offsetY;

    public IconButton() {
        super(0, 0, 16, 16, Component.empty(), IconButton::onClick, Button.DEFAULT_NARRATION);
    }

    public void addAction(PicData icon, IntConsumer onPress, List<Component> tooltip) {
        this.actions.add(new ActionPair(icon, onPress, tooltip));
    }

    public void addAction(PicData icon, Runnable onPress, List<Component> tooltip) {
        this.actions.add(new ActionPair(icon, __ -> onPress.run(), tooltip));
    }

    public void setStatus(int status) {
        this.index = status;
    }

    public void setBackground(PicData background) {
        this.background = background;
    }

    public void setBackgroundHighlight(PicData backgroundHighlight) {
        this.backgroundHighlight = backgroundHighlight;
    }

    private static void onClick(Button button) {
        if (button instanceof IconButton iconButton) {
            if (iconButton.index < 0 || iconButton.index >= iconButton.actions.size()) {
                return;
            }
            iconButton.actions.get(iconButton.index).onClick().accept(iconButton.index);
            iconButton.index = (iconButton.index + 1) % iconButton.actions.size();
        } else {
            button.onPress();
        }
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        if (this.index < 0 || this.index >= this.actions.size()) {
            return;
        }
        PicData bg = this.isHovered() ? GameUtil.coalesce(this.backgroundHighlight, this.background) : GameUtil.coalesce(this.background, this.backgroundHighlight);
        if (bg != null) {
            this.background.render(graphics, this.getX(), this.getY());
        }
        var icon = this.actions.get(this.index).icon();
        icon.render(graphics, this.getX(), this.getY());
    }

    @Override
    public void setOffset(int x, int y) {
        this.offsetX = x;
        this.offsetY = y;
    }

    @Override
    public int getX() {
        return super.getX() + this.offsetX;
    }

    @Override
    public int getY() {
        return super.getY() + this.offsetY;
    }

    public void setPosition(int x, int y) {
        this.setX(x);
        this.setY(y);
    }

    @Override
    public List<Component> getTooltipMessage(boolean isShift) {
        if (this.index < 0 || this.index >= this.actions.size()) {
            return List.of();
        }
        return GameUtil.coalesce(this.actions.get(this.index).tooltip(), List.of());
    }

    @Override
    public boolean shouldDisplay(int mouseX, int mouseY) {
        if (this.contains(mouseX, mouseY)) {
            if (this.index < 0 || this.index >= this.actions.size()) {
                return false;
            }
            var tooltip = this.actions.get(this.index).tooltip();
            return tooltip != null && !tooltip.isEmpty();
        }
        return false;
    }

    public boolean contains(double x, double y) {
        return x >= this.getX() && x <= this.getX() + this.getWidth() && y >= this.getY() && y <= this.getY() + this.getHeight();
    }

    private record ActionPair(PicData icon, IntConsumer onClick, List<Component> tooltip) {

    }

}
