package com.glodblock.github.modularbees.dynamic;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
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

    public void addBlockTag(TagKey<Block> tag, ResourceLocation id) {
        this.addBlockTag(tag, new JsonPrimitive(id.toString()));
    }

    public void addBlockTag(TagKey<Block> tag, JsonElement id) {
        this.addTag(tag, "blocks", id);
    }

    public void addItemTag(TagKey<Item> tag, ResourceLocation id) {
        this.addItemTag(tag, new JsonPrimitive(id.toString()));
    }

    public void addItemTag(TagKey<Item> tag, JsonElement id) {
        this.addTag(tag, "items", id);
    }

    public void addTag(TagKey<?> tag, String domain, JsonElement id) {
        var tagPath = this.getTagLocation(domain, tag.location());
        var tagArr = this.lateJson.computeIfAbsent(tagPath, k -> {
            var json = new JsonObject();
            json.add("values", new JsonArray());
            return json;
        }).getAsJsonArray("values");
        tagArr.add(id);
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

    public ResourceLocation getRecipeLocation(ResourceLocation recipeId) {
        return ResourceLocation.fromNamespaceAndPath(recipeId.getNamespace(), String.join("", "recipes/", recipeId.getPath(), ".json"));
    }

    public ResourceLocation getAdvancementLocation(ResourceLocation advancementId) {
        return ResourceLocation.fromNamespaceAndPath(advancementId.getNamespace(), String.join("", "advancements/", advancementId.getPath(), ".json"));
    }

    public ResourceLocation getTagLocation(String identifier, ResourceLocation tagId) {
        return ResourceLocation.fromNamespaceAndPath(tagId.getNamespace(), String.join("", "tags/", identifier, "/", tagId.getPath(), ".json"));
    }

    public ResourceLocation getBlockLootLocation(ResourceLocation rootId) {
        return ResourceLocation.fromNamespaceAndPath(rootId.getNamespace(), String.join("", "loot_tables/blocks/", rootId.getPath(), ".json"));
    }

}