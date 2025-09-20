package com.glodblock.github.modularbees.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.patchouli.neoforge.client.NeoForgeClientXplatImpl;

@Mixin(NeoForgeClientXplatImpl.class)
public abstract class NeoForgeClientXplatImplMixin {

    @Unique
    private ModelData mb$modelData;

    @Inject(
            method = "renderForMultiblock",
            at = @At("HEAD"),
            remap = false
    )
    private void collectModelData(BlockState state, BlockPos pos, BlockAndTintGetter multiblock, PoseStack ps, MultiBufferSource buffers, RandomSource rand, CallbackInfo ci) {
        this.mb$modelData = ModelData.EMPTY;
        var blockRenderer = Minecraft.getInstance().getBlockRenderer();
        var model = blockRenderer.getBlockModel(state);
        this.mb$modelData = model.getModelData(multiblock, pos, state, this.mb$modelData);
    }

    @Redirect(
            method = "renderForMultiblock",
            at = @At(value = "FIELD", target = "Lnet/neoforged/neoforge/client/model/data/ModelData;EMPTY:Lnet/neoforged/neoforge/client/model/data/ModelData;"),
            remap = false
    )
    private ModelData replaceModelData() {
        return this.mb$modelData;
    }

}
