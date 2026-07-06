package com.glodblock.github.modularbees.client.model;

import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public record ModularModelData(Identifier border, @Nullable Identifier particle, Object[] faces) implements UnbakedModel {

}
