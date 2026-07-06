package com.glodblock.github.modularbees.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ModularConnectModelLoader implements UnbakedModelLoader<@NotNull ModularModelData> {

    public static final ModularConnectModelLoader LOADER = new ModularConnectModelLoader();

    protected ModularConnectModelLoader() {

    }

    @Override
    public @NotNull ModularModelData read(@NotNull JsonObject json, @NotNull JsonDeserializationContext context) throws JsonParseException {
        Identifier border = this.getResource(json.get("border"));
        Identifier particle = null;
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
        return new ModularModelData(border, particle, faces.toArray());
    }

    public Identifier getResource(JsonElement json) {
        return Identifier.parse(json.getAsString());
    }

}
