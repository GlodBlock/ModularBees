package com.glodblock.github.modularbees.client.util;

import com.glodblock.github.glodium.client.render.ColorData;
import com.glodblock.github.modularbees.ModularBees;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;

public final class PicData {

    public static final RenderPipeline GUI_TEXTURED_OPAQUE = RenderPipelines.GUI_TEXTURED.toBuilder()
            .withLocation(ModularBees.id("pipeline/gui_textured_opaque"))
            .withColorTargetState(ColorTargetState.DEFAULT)
            .build();
    public static final int DEFAULT_TEXTURE_WIDTH = 256;
    public static final int DEFAULT_TEXTURE_HEIGHT = 256;
    private final Identifier texture;
    private final int fullWidth;
    private final int fullHeight;
    private Rect2i selectBox;
    private Rect2i renderPos;
    private int z = 0;
    private boolean needBlend = true;
    private ColorData color = new ColorData(0xFFFFFFFF);

    private PicData(Identifier texture, int w, int h) {
        this.texture = texture;
        this.fullWidth = w;
        this.fullHeight = h;
    }

    public static PicData of(Identifier texture, int width, int height) {
        return new PicData(texture, width, height);
    }

    public static PicData of(Identifier texture) {
        return new PicData(texture, DEFAULT_TEXTURE_WIDTH, DEFAULT_TEXTURE_HEIGHT);
    }

    public static PicData of(TextureAtlasSprite texture) {
        var atlas = (TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(texture.atlasLocation());
        return new PicData(texture.atlasLocation(), atlas.width, atlas.height)
                .select(texture.getX() + texture.padding,
                        texture.getY() + texture.padding,
                        texture.contents().width(),
                        texture.contents().height());
    }

    public PicData select(int x, int y, int w, int h) {
        this.selectBox = new Rect2i(x, y, w, h);
        return this;
    }

    public PicData select(Rect2i box) {
        return this.select(box.getX(), box.getY(), box.getWidth(), box.getHeight());
    }

    public PicData pos(int x, int y, int w, int h) {
        this.renderPos = new Rect2i(x, y, w, h);
        return this;
    }

    public PicData pos(int x, int y) {
        return this.pos(x, y, 0, 0);
    }

    public PicData pos(Rect2i box) {
        if (box == null) {
            this.renderPos = null;
            return this;
        }
        return this.pos(box.getX(), box.getY(), box.getWidth(), box.getHeight());
    }

    public PicData z(int z) {
        this.z = z;
        return this;
    }

    public PicData color(@NotNull ColorData color) {
        this.color = new ColorData(color.toARGB());
        return this;
    }

    public PicData color(int argb) {
        this.color = new ColorData(argb);
        return this;
    }

    public PicData needBlend(boolean value) {
        this.needBlend = value;
        return this;
    }

    public PicData copy() {
        var pic = new PicData(this.texture, this.fullWidth, this.fullHeight);
        return pic.select(this.selectBox)
                .pos(this.renderPos)
                .z(this.z)
                .color(this.color)
                .needBlend(this.needBlend);
    }

    public PicData copy(Rect2i select) {
        var pic = new PicData(this.texture, this.fullWidth, this.fullHeight);
        return pic.select(select)
                .pos(this.renderPos)
                .z(this.z)
                .color(this.color)
                .needBlend(this.needBlend);
    }

    public PicData copy(int x, int y, int w, int h) {
        var pic = new PicData(this.texture, this.fullWidth, this.fullHeight);
        return pic.select(x, y, w, h)
                .pos(this.renderPos)
                .z(this.z)
                .color(this.color)
                .needBlend(this.needBlend);
    }

    public Rect2i getSelect() {
        return this.selectBox;
    }

    public Rect2i getRenderPos() {
        return this.renderPos;
    }

    public void render(GuiGraphicsExtractor graphics) {
        this.render(graphics, this.selectBox, this.renderPos);
    }

    public void render(GuiGraphicsExtractor graphics, int posX, int posY) {
        this.render(graphics, this.selectBox, new Rect2i(posX, posY, 0, 0));
    }

    public void render(GuiGraphicsExtractor graphics, Rect2i selectBox, Rect2i renderPos) {
        if (selectBox == null) {
            throw new IllegalArgumentException("Need to select rendering area before rendering.");
        }
        if (renderPos == null) {
            throw new IllegalArgumentException("Need to set rendering position before rendering.");
        }
        var pipeline = this.needBlend ? RenderPipelines.GUI_TEXTURED : GUI_TEXTURED_OPAQUE;
        var pic = Minecraft.getInstance().getTextureManager().getTexture(this.texture);
        var minU = selectBox.getX() / (float) this.fullWidth;
        var minV = selectBox.getY() / (float) this.fullHeight;
        var maxU = (selectBox.getX() + selectBox.getWidth()) / (float) this.fullWidth;
        var maxV = (selectBox.getY() + selectBox.getHeight()) / (float) this.fullHeight;
        float x1 = renderPos.getX();
        float y1 = renderPos.getY();
        float x2 = x1, y2 = y1;
        if (renderPos.getWidth() <= 0) {
            x2 += selectBox.getWidth();
        } else {
            x2 += renderPos.getWidth();
        }
        if (renderPos.getHeight() <= 0) {
            y2 += selectBox.getHeight();
        } else {
            y2 += renderPos.getHeight();
        }
        graphics.submitGuiElementRenderState(new BlitRenderState(
                pipeline,
                TextureSetup.singleTexture(pic.getTextureView(), pic.getSampler()),
                new Matrix3x2f(graphics.pose()),
                (int) x1, (int) y1,
                (int) x2, (int) y2,
                minU, maxU,
                minV, maxV,
                this.color.toARGB(),
                graphics.peekScissorStack())
        );
    }

}
