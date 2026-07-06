package com.glodblock.github.modularbees.dynamic;

import com.google.gson.JsonElement;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DyResourcePack extends DynamicPack {

    public DyResourcePack() {
        super(PackType.CLIENT_RESOURCES);
    }

    public void addBlockModel(Identifier loc, JsonElement obj) {
        this.data.put(this.getModelLocation(loc), obj.toString().getBytes(StandardCharsets.UTF_8));
    }

    public void addItemModel(Identifier loc, JsonElement obj) {
        this.data.put(this.getItemModelLocation(loc), obj.toString().getBytes(StandardCharsets.UTF_8));
    }

    public void addBlockState(Identifier loc, JsonElement obj) {
        this.data.put(this.getBlockStateLocation(loc), obj.toString().getBytes(StandardCharsets.UTF_8));
    }

    public void addItemState(Identifier loc, JsonElement obj) {
        this.data.put(this.getItemStateLocation(loc), obj.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected Map<Identifier, byte[]> createMap() {
        return new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getMetadataSection(@NotNull MetadataSectionType<@NotNull T> metaReader) {
        if (metaReader.equals(PackMetadataSection.CLIENT_TYPE)) {
            return (T) new PackMetadataSection(Component.literal("Modular Bees assets"), SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES).minorRange());
        } else {
            return null;
        }
    }

    public Identifier getBlockStateLocation(Identifier blockId) {
        return Identifier.fromNamespaceAndPath(blockId.getNamespace(), String.join("", "blockstates/", blockId.getPath(), ".json"));
    }

    public Identifier getItemStateLocation(Identifier itemId) {
        return Identifier.fromNamespaceAndPath(itemId.getNamespace(), String.join("", "items/", itemId.getPath(), ".json"));
    }

    public Identifier getModelLocation(Identifier blockId) {
        return Identifier.fromNamespaceAndPath(blockId.getNamespace(), String.join("", "models/block/", blockId.getPath(), ".json"));
    }

    public Identifier getItemModelLocation(Identifier itemId) {
        return Identifier.fromNamespaceAndPath(itemId.getNamespace(), String.join("", "models/item/", itemId.getPath(), ".json"));
    }

}
