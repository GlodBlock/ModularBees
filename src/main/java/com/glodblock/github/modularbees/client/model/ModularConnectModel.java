package com.glodblock.github.modularbees.client.model;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public record ModularConnectModel(Identifier main, Variant.SimpleModelState modelState) implements CustomUnbakedBlockStateModel {

    public static MapCodec<ModularConnectModel> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Identifier.CODEC.fieldOf("model").forGetter(ModularConnectModel::main),
            Variant.SimpleModelState.MAP_CODEC.forGetter(ModularConnectModel::modelState))
            .apply(builder, ModularConnectModel::new)
    );

    @Override
    public void resolveDependencies(@NotNull Resolver resolver) {
        resolver.markDependency(this.main);
    }

    @Override
    public @NotNull BlockStateModel bake(@NotNull ModelBaker baker) {
        var unbaked = baker.getModel(this.main).wrapped();
        if (unbaked instanceof ModularModelData(Identifier border, Identifier particle, Object[] faces)) {
            for (var face : faces) {
                if (!(face instanceof Identifier || face instanceof Direction)) {
                    throw new IllegalArgumentException(Arrays.toString(faces) + " contains unexpected types.");
                }
            }
            return new ModularConnectBakedModel(baker.materials(), this.modelState.asModelState(), border, particle, faces);
        } else {
            throw new IllegalStateException("%s (%s) can't be casted to ModularModelData".formatted(this.main, unbaked.getClass()));
        }
    }

    @Override
    public @NotNull MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
        return CODEC;
    }

}
