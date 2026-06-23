package com.glodblock.github.modularbees.dynamic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DyDataPack extends DynamicPack {

    private final Object2ReferenceMap<ResourceLocation, JsonObject> lateJson = new Object2ReferenceOpenHashMap<>();

    public DyDataPack() {
        super(PackType.SERVER_DATA);
    }

    @Override
    public DynamicPack build(PackLocationInfo name) {
        for (var entry : this.lateJson.entrySet()) {
            this.data.put(entry.getKey(), entry.getValue().toString().getBytes(StandardCharsets.UTF_8));
        }
        this.lateJson.clear();
        return super.build(name);
    }

    public void addBlockLootTable(ResourceLocation loc, JsonElement obj) {
        this.data.put(this.getBlockLootLocation(loc), obj.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected Map<ResourceLocation, byte[]> createMap() {
        return new Object2ReferenceOpenHashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getMetadataSection(@NotNull MetadataSectionSerializer<T> metaReader) {
        if (metaReader == PackMetadataSection.TYPE) {
            return (T) new PackMetadataSection(Component.literal("Modular Bees data"), SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA));
        }
        return null;
    }

    public ResourceLocation getBlockLootLocation(ResourceLocation rootId) {
        return ResourceLocation.fromNamespaceAndPath(rootId.getNamespace(), String.join("", "loot_table/blocks/", rootId.getPath(), ".json"));
    }

}