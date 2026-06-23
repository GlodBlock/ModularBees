package com.glodblock.github.modularbees.client.model;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Function;

public class ModularConnectModel implements IUnbakedGeometry<ModularConnectModel> {

    private final ResourceLocation border;
    private final ResourceLocation particle;
    private final Object[] faces;

    public ModularConnectModel(ResourceLocation border, @Nullable ResourceLocation particle, Object[] faces) {
        for (var face : faces) {
            if (!(face instanceof ResourceLocation || face instanceof Direction)) {
                throw new IllegalArgumentException(Arrays.toString(faces) + " contains unexpected types.");
            }
        }
        this.border = border;
        this.particle = particle;
        this.faces = faces;
    }

    @Override
    public @NotNull BakedModel bake(@NotNull IGeometryBakingContext context, @NotNull ModelBaker baker, @NotNull Function<Material, TextureAtlasSprite> getter, @NotNull ModelState modelState, @NotNull ItemOverrides overrides) {
        return new ModularConnectBakedModel(getter, modelState, this.border, this.particle, this.faces);
    }

}
