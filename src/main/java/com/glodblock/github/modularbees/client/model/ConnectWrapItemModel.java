package com.glodblock.github.modularbees.client.model;

import com.glodblock.github.modularbees.client.util.StandardItemTransform;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public record ConnectWrapItemModel(ModularConnectBakedModel model) implements ItemModel {

    public static MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Identifier.CODEC.fieldOf("model").forGetter(Unbaked::main))
            .apply(builder, Unbaked::new)
    );

    @Override
    public void update(@NotNull ItemStackRenderState output, @NotNull ItemStack item, @NotNull ItemModelResolver resolver, @NotNull ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        output.appendModelIdentityElement(this.model);
        final List<BlockStateModelPart> parts = new ArrayList<>();
        this.model.collectParts(BlockAndTintGetter.EMPTY, BlockPos.ZERO, Blocks.AIR.defaultBlockState(), RandomSource.create(seed), parts);
        var layer = output.newLayer();
        layer.setParticleMaterial(this.model.particleMaterial());
        layer.setUsesBlockLight(true);
        layer.setItemTransform(StandardItemTransform.INSTANCE.getTransform(displayContext));
        layer.setupSpecialModel(new SpecialModelRenderer<>() {
            @Override
            public void submit(@Nullable Object argument, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
                var qi = new QuadInstance();
                qi.setLightCoords(lightCoords);
                qi.setOverlayCoords(overlayCoords);
                for (var part : parts) {
                    var renderType = (part.materialFlags() & BakedQuad.FLAG_TRANSLUCENT) != 0 ? Sheets.translucentBlockItemSheet() : Sheets.cutoutBlockItemSheet();
                    submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
                        for (var cullFace : Direction.values()) {
                            for (var quad : part.getQuads(cullFace)) {
                                buffer.putBakedQuad(pose, quad, qi);
                            }
                        }
                    });
                }
            }

            @Override
            public void getExtents(@NotNull Consumer<Vector3fc> output) {
                output.accept(new Vector3f(0.0F, 0.0F, 0.0F));
                output.accept(new Vector3f(1.0F, 1.0F, 1.0F));
            }

            @Override
            public @Nullable Object extractArgument(@NotNull ItemStack stack) {
                return null;
            }
        }, null);
    }

    public record Unbaked(Identifier main) implements ItemModel.Unbaked {

        @Override
        public @NotNull MapCodec<? extends ItemModel.Unbaked> type() {
            return CODEC;
        }

        @Override
        public @NotNull ItemModel bake(@NotNull BakingContext context, @NotNull Matrix4fc transformation) {
            var unbaked = context.blockModelBaker().getModel(this.main).wrapped();
            if (unbaked instanceof ModularModelData(Identifier border, Identifier particle, Object[] faces)) {
                for (var face : faces) {
                    if (!(face instanceof Identifier || face instanceof Direction)) {
                        throw new IllegalArgumentException(Arrays.toString(faces) + " contains unexpected types.");
                    }
                }
                return new ConnectWrapItemModel(new ModularConnectBakedModel(context.blockModelBaker().materials(), BlockModelRotation.IDENTITY, border, particle, faces));
            } else {
                throw new IllegalStateException("%s (%s) can't be casted to ModularModelData".formatted(this.main, unbaked.getClass()));
            }
        }

        @Override
        public void resolveDependencies(@NotNull Resolver resolver) {
            resolver.markDependency(this.main);
        }

    }

}
