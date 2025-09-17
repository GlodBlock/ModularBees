package com.glodblock.github.modularbees.client.gui.elements;

import com.glodblock.github.modularbees.client.util.PicData;
import com.glodblock.github.modularbees.client.util.RelativeRect2i;
import com.glodblock.github.modularbees.client.util.TooltipElement;
import com.glodblock.github.modularbees.util.GameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class FluidTankDisplay extends RelativeRect2i implements Renderable, TooltipElement {

    protected final Supplier<FluidStack> fluid;
    protected int capacity = 0;
    protected boolean showAmount = true;

    public FluidTankDisplay(Supplier<FluidStack> fluid) {
        this.fluid = fluid;
    }

    public FluidTankDisplay capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public FluidTankDisplay showAmount(boolean value) {
        this.showAmount = value;
        return this;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.contains(mouseX, mouseY)) {
            graphics.fillGradient(RenderType.guiOverlay(), this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x80FFFFFF, 0x80FFFFFF, 0);
        }
        var fluid = this.fluid.get();
        if (fluid == null || fluid.isEmpty() || this.capacity == 0) {
            return;
        }
        var attributes = IClientFluidTypeExtensions.of(fluid.getFluid());
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(attributes.getStillTexture(fluid));
        var texture = PicData.of(sprite).color(attributes.getTintColor(fluid));
        int maxX = this.width;
        int maxY = this.getDisplayHeight(fluid.getAmount());
        var unit = texture.getSelect();
        for (int y = 0; y < maxY; y += unit.getHeight()) {
            for (int x = 0; x < maxX; x += unit.getWidth()) {
                var showX = Math.min(maxX - x, unit.getWidth());
                var showY = Math.min(maxY - y, unit.getHeight());
                texture.render(graphics,
                        new Rect2i(unit.getX(), unit.getY(), showX, showY),
                        new Rect2i(x + this.getX(), this.height - maxY + y + this.getY(), 0, 0)
                );
            }
        }
    }

    protected int getDisplayHeight(int amount) {
        if (this.showAmount) {
            return Math.min(this.height, this.height * amount / this.capacity);
        }
        return this.height;
    }

    @Override
    public List<Component> getTooltipMessage(boolean isShift) {
        var fluid = this.fluid.get();
        if (fluid == null || fluid.isEmpty() || this.capacity == 0) {
            return List.of(
                    Component.translatable("modularbees.gui.empty"),
                    Component.translatable("modularbees.gui.fluid_tank.amount", 0, GameUtil.NUMBER_F.format(this.capacity))
            );
        } else {
            return List.of(
                    fluid.getHoverName(),
                    Component.translatable("modularbees.gui.fluid_tank.amount", GameUtil.NUMBER_F.format(fluid.getAmount()), GameUtil.NUMBER_F.format(this.capacity))
            );
        }
    }

    @Override
    public boolean shouldDisplay(int mouseX, int mouseY) {
        return this.contains(mouseX, mouseY);
    }

}
