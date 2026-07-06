package com.glodblock.github.modularbees.client.model;

import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public class ModularConnectBakedModel extends ConnectBorderlineBakedModel {

    private final EnumMap<Direction, Material.Baked> sides = new EnumMap<>(Direction.class);
    private final Material.Baked defaultAtlas;
    private final Material.Baked particle;

    public ModularConnectBakedModel(MaterialBaker getter, ModelState modelTransform, Identifier border, @Nullable Identifier particle, Object[] faces) {
        super(getter, modelTransform, border);
        ModelDebugName debugName = getClass()::toString;
        Material.Baked atlas = null;
        for (Object obj : faces) {
            if (obj instanceof Identifier location) {
                atlas = getter.get(new Material(location), debugName);
            } else if (obj instanceof Direction face) {
                if (atlas == null) {
                    throw new IllegalArgumentException("Need to input texture first.");
                }
                this.sides.put(face, atlas);
            }
        }
        this.defaultAtlas = atlas;
        this.particle = particle == null ? this.defaultAtlas : getter.get(new Material(particle), debugName);
    }

    @Override
    public @NotNull Material.Baked particleMaterial() {
        return this.particle == null ? super.particleMaterial() : this.particle;
    }

    @Override
    Material.Baked getFaceSprite(Direction side) {
        return this.sides.getOrDefault(side, this.defaultAtlas);
    }

}
