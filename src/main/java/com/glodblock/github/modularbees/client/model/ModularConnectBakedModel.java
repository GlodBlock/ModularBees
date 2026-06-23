package com.glodblock.github.modularbees.client.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.function.Function;

public class ModularConnectBakedModel extends ConnectBorderlineBakedModel {

    private final EnumMap<Direction, TextureAtlasSprite> sides = new EnumMap<>(Direction.class);
    private final TextureAtlasSprite defaultAtlas;
    private final TextureAtlasSprite particle;

    public ModularConnectBakedModel(Function<Material, TextureAtlasSprite> getter, ModelState modelTransform, ResourceLocation border, @Nullable ResourceLocation particle, Object[] faces) {
        super(getter, modelTransform, border);
        TextureAtlasSprite atlas = null;
        for (Object obj : faces) {
            if (obj instanceof ResourceLocation location) {
                atlas = getter.apply(new Material(InventoryMenu.BLOCK_ATLAS, location));
            } else if (obj instanceof Direction face) {
                if (atlas == null) {
                    throw new IllegalArgumentException("Need to input texture first.");
                }
                this.sides.put(face, atlas);
            }
        }
        this.defaultAtlas = atlas;
        this.particle = particle == null ? this.defaultAtlas : getter.apply(new Material(InventoryMenu.BLOCK_ATLAS, particle));
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return this.particle == null ? super.getParticleIcon() : this.particle;
    }

    @Override
    TextureAtlasSprite getFaceSprite(Direction side) {
        return this.sides.getOrDefault(side, this.defaultAtlas);
    }

}
