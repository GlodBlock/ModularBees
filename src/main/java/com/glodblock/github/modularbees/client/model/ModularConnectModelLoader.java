package com.glodblock.github.modularbees.client.model;

import com.glodblock.github.modularbees.client.util.StandardItemTransform;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ModularConnectModelLoader implements IGeometryLoader<ModularConnectModel> {

    public static final ModularConnectModelLoader LOADER = new ModularConnectModelLoader();

    protected ModularConnectModelLoader() {

    }

    @Override
    public @NotNull ModularConnectModel read(@NotNull JsonObject json, @NotNull JsonDeserializationContext context) throws JsonParseException {
        StandardItemTransform.init(context);
        ResourceLocation border = this.getResource(json.get("border"));
        ResourceLocation particle = null;
        List<Object> faces = new ArrayList<>();
        for (var dir : Direction.values()) {
            if (json.has(dir.getSerializedName())) {
                faces.add(this.getResource(json.get(dir.getSerializedName())));
                faces.add(dir);
            }
        }
        if (json.has("top")) {
            faces.add(this.getResource(json.get("top")));
            faces.add(Direction.UP);
        }
        if (json.has("sides")) {
            faces.add(this.getResource(json.get("sides")));
            faces.add(Direction.WEST);
            faces.add(Direction.EAST);
            faces.add(Direction.SOUTH);
            faces.add(Direction.NORTH);
        }
        if (json.has("down")) {
            faces.add(this.getResource(json.get("down")));
            faces.add(Direction.DOWN);
        }
        if (json.has("front")) {
            faces.add(this.getResource(json.get("front")));
            faces.add(Direction.NORTH);
        }
        if (json.has("all")) {
            faces.add(this.getResource(json.get("all")));
            faces.add(Direction.WEST);
            faces.add(Direction.EAST);
            faces.add(Direction.SOUTH);
            faces.add(Direction.NORTH);
            faces.add(Direction.UP);
            faces.add(Direction.DOWN);
        }
        if (json.has("default")) {
            faces.addLast(this.getResource(json.get("default")));
        }
        if (json.has("particle")) {
            particle = this.getResource(json.get("particle"));
        }
        return new ModularConnectModel(border, particle, faces.toArray());
    }

    public ResourceLocation getResource(JsonElement json) {
        return ResourceLocation.parse(json.getAsString());
    }

}
