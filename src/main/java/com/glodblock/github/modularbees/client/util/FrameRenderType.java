package com.glodblock.github.modularbees.client.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class FrameRenderType {

    public static final RenderType TYPE = cutoutNoDepthWrite();

    private static RenderType cutoutNoDepthWrite() {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.RENDERTYPE_CUTOUT_MIPPED_SHADER)
                .setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
                .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, false))
                .createCompositeState(true);

        return RenderType.create(
                "cutout_no_depth_write",
                DefaultVertexFormat.BLOCK,
                VertexFormat.Mode.QUADS,
                262144,
                true,  // needs sorting
                false, // affects outline
                state
        );
    }

}
