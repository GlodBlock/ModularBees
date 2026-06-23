package com.glodblock.github.modularbees.client.gui.elements;

import com.glodblock.github.modularbees.client.util.PicData;
import com.glodblock.github.modularbees.client.util.RelativeRect2i;
import com.glodblock.github.modularbees.client.util.TooltipElement;
import com.glodblock.github.modularbees.util.GameUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.IntSupplier;

public class EnergyDisplay extends RelativeRect2i implements Renderable, TooltipElement {

    protected final IntSupplier energy;
    protected int capacity = 0;
    protected PicData bar;

    public EnergyDisplay(IntSupplier energy) {
        this.energy = energy;
    }

    public EnergyDisplay capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public EnergyDisplay texture(PicData texture) {
        this.bar = texture;
        return this;
    }

    @Override
    public List<Component> getTooltipMessage(boolean isShift) {
        return List.of(Component.translatable("modularbees.gui.energy_bar.amount", GameUtil.NUMBER_F.format(this.energy.getAsInt()), GameUtil.NUMBER_F.format(this.capacity)));
    }

    @Override
    public boolean shouldDisplay(int mouseX, int mouseY) {
        return this.contains(mouseX, mouseY);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        var energy = this.energy.getAsInt();
        if (energy <= 0 || this.bar == null || this.capacity <= 0) {
            return;
        }
        int heightBackground = Math.min(this.height, this.height * energy / this.capacity);
        int startBackground = this.getHeight() - Math.min(this.height, this.height * energy / this.capacity);
        var barBox = this.bar.getSelect();
        int heightBar = Math.min(barBox.getHeight(), barBox.getHeight() * energy / this.capacity);
        int startBar = barBox.getHeight() - heightBar;
        this.bar.render(graphics,
                new Rect2i(barBox.getX(), barBox.getY() + startBar, barBox.getWidth(), heightBar),
                new Rect2i(this.getX(), this.getY() + startBackground, this.width, heightBackground)
        );
    }

}
