package com.glodblock.github.modularbees.common.items;

import com.glodblock.github.modularbees.dynamic.DyDataPack;
import com.glodblock.github.modularbees.dynamic.DyResourcePack;
import com.glodblock.github.modularbees.util.DataProvider;
import com.glodblock.github.modularbees.util.RegisterTask;
import com.glodblock.github.modularbees.util.ResourceProvider;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ItemMB extends Item implements RegisterTask, ResourceProvider, DataProvider {

    private ResourceLocation registryName;

    public ItemMB(Properties properties) {
        super(properties);
    }

    public ItemMB() {
        super(new Item.Properties());
    }

    @Override
    public void load(DyDataPack pack) {

    }

    @Override
    public void onRegister(ResourceLocation id) {
        this.registryName = id;
    }

    @Override
    public void load(DyResourcePack pack) {
        this.loadModel(pack);
    }

    protected void loadModel(DyResourcePack pack) {
        pack.addItemModel(this.registryName, createModelJson(this.registryName));
    }

    protected JsonObject createModelJson(ResourceLocation id) {
        var parent = new JsonObject();
        parent.addProperty("parent", "minecraft:item/generated");
        {
            var layer = new JsonObject();
            layer.addProperty("layer0", addPrefix(id));
            parent.add("textures", layer);
        }
        return parent;
    }

    protected String addPrefix(ResourceLocation id) {
        String[] s = id.toString().split(":");
        return s[0] + ":item/" + s[1];
    }

}
