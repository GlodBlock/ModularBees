package com.glodblock.github.modularbees.client.util;

import com.glodblock.github.glodium.client.render.ColorData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public final class PicData {

    public static final int DEFAULT_TEXTURE_WIDTH = 256;
    public static final int DEFAULT_TEXTURE_HEIGHT = 256;
    private final ResourceLocation texture;
    private final int fullWidth;
    private final int fullHeight;
    private Rect2i selectBox;
    private Rect2i renderPos;
    private int z = 0;
    private boolean needBlend = true;
    private ColorData color = new ColorData(0xFFFFFFFF);

    private PicData(ResourceLocation texture, int w, int h) {
        this.texture = texture;
        this.fullWidth = w;
        this.fullHeight = h;
    }

    public static PicData of(ResourceLocation texture, int width, int height) {
        return new PicData(texture, width, height);
    }

    public static PicData of(ResourceLocation texture) {
        return new PicData(texture, DEFAULT_TEXTURE_WIDTH, DEFAULT_TEXTURE_HEIGHT);
    }

    public static PicData of(TextureAtlasSprite texture) {
        var atlas = (TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(texture.atlasLocation());
        return new PicData(texture.atlasLocation(), atlas.width, atlas.height)
                .select(texture.getX(),
                        texture.getY(),
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

    public void render(GuiGraphics graphics) {
        this.render(graphics, this.selectBox, this.renderPos);
    }

    public void render(GuiGraphics graphics, int posX, int posY) {
        this.render(graphics, this.selectBox, new Rect2i(posX, posY, 0, 0));
    }

    public void render(GuiGraphics graphics, Rect2i selectBox, Rect2i renderPos) {
        if (selectBox == null) {
            throw new IllegalArgumentException("Need to select rendering area before rendering.");
        }
        if (renderPos == null) {
            throw new IllegalArgumentException("Need to set rendering position before rendering.");
        }
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, this.texture);
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
        Matrix4f matrix = graphics.pose().last().pose();
        var buf = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buf.addVertex(matrix, x1, y2, this.z).setUv(minU, maxV).setColor(this.color.toARGB());
        buf.addVertex(matrix, x2, y2, this.z).setUv(maxU, maxV).setColor(this.color.toARGB());
        buf.addVertex(matrix, x2, y1, this.z).setUv(maxU, minV).setColor(this.color.toARGB());
        buf.addVertex(matrix, x1, y1, this.z).setUv(minU, minV).setColor(this.color.toARGB());
        if (this.needBlend) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        } else {
            RenderSystem.disableBlend();
        }
        BufferUploader.drawWithShader(buf.buildOrThrow());
    }

}
