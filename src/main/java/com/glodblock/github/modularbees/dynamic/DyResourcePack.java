package com.glodblock.github.modularbees.dynamic;

import com.google.gson.JsonElement;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
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

    public void addBlockModel(ResourceLocation loc, JsonElement obj) {
        this.data.put(this.getModelLocation(loc), obj.toString().getBytes(StandardCharsets.UTF_8));
    }

    public void addItemModel(ResourceLocation loc, JsonElement obj) {
        this.data.put(this.getItemModelLocation(loc), obj.toString().getBytes(StandardCharsets.UTF_8));
    }

    public void addBlockState(ResourceLocation loc, JsonElement obj) {
        this.data.put(this.getBlockStateLocation(loc), obj.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected Map<ResourceLocation, byte[]> createMap() {
        return new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getMetadataSection(@NotNull MetadataSectionSerializer<T> metaReader) {
        if (metaReader == PackMetadataSection.TYPE) {
            return (T) new PackMetadataSection(Component.literal("Modular Bees assets"), SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES));
        }
        return null;
    }

    public ResourceLocation getBlockStateLocation(ResourceLocation blockId) {
        return ResourceLocation.fromNamespaceAndPath(blockId.getNamespace(), String.join("", "blockstates/", blockId.getPath(), ".json"));
    }

    public ResourceLocation getModelLocation(ResourceLocation blockId) {
        return ResourceLocation.fromNamespaceAndPath(blockId.getNamespace(), String.join("", "models/block/", blockId.getPath(), ".json"));
    }

    public ResourceLocation getItemModelLocation(ResourceLocation itemId) {
        return ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(), String.join("", "models/item/", itemId.getPath(), ".json"));
    }

}
