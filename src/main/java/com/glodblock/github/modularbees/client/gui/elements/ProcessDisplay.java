package com.glodblock.github.modularbees.client.gui.elements;

import com.glodblock.github.modularbees.client.util.PicData;
import com.glodblock.github.modularbees.client.util.RelativeRect2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.NotNull;

import java.util.function.DoubleSupplier;

public class ProcessDisplay extends RelativeRect2i implements Renderable {

    protected final DoubleSupplier process;
    protected int full = 0;
    protected PicData bar;

    public ProcessDisplay(DoubleSupplier process) {
        this.process = process;
    }

    public ProcessDisplay full(int full) {
        this.full = full;
        return this;
    }

    public ProcessDisplay texture(PicData texture) {
        this.bar = texture;
        return this;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        var process = this.process.getAsDouble();
        if (process <= 0 || this.bar == null || this.full <= 0) {
            return;
        }
        int widthBackground = (int) Math.min(this.width, this.width * process / this.full);
        var barBox = this.bar.getSelect();
        int widthBar = (int) Math.min(barBox.getWidth(), barBox.getWidth() * process / this.full);
        this.bar.render(graphics,
                new Rect2i(barBox.getX(), barBox.getY(), widthBar, barBox.getHeight()),
                new Rect2i(this.getX(), this.getY(), widthBackground, this.height)
        );
    }

}