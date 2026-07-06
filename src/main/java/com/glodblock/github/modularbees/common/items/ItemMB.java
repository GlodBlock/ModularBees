package com.glodblock.github.modularbees.common.items;

import com.glodblock.github.modularbees.dynamic.DyDataPack;
import com.glodblock.github.modularbees.dynamic.DyResourcePack;
import com.glodblock.github.modularbees.util.DataProvider;
import com.glodblock.github.modularbees.util.RegisterTask;
import com.glodblock.github.modularbees.util.ResourceProvider;
import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class ItemMB extends Item implements RegisterTask, ResourceProvider, DataProvider {

    private Identifier registryName;

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
    public void onRegister(Identifier id) {
        this.registryName = id;
    }

    @Override
    public void load(DyResourcePack pack) {
        this.loadState(pack);
        this.loadModel(pack);
    }

    protected void loadState(DyResourcePack pack) {
        pack.addItemState(this.registryName, createStateJson(this.registryName));
    }

    protected void loadModel(DyResourcePack pack) {
        pack.addItemModel(this.registryName, createModelJson(this.registryName));
    }

    protected JsonObject createModelJson(Identifier id) {
        var parent = new JsonObject();
        parent.addProperty("parent", "minecraft:item/generated");
        {
            var layer = new JsonObject();
            layer.addProperty("layer0", addPrefix(id));
            parent.add("textures", layer);
        }
        return parent;
    }

    protected JsonObject createStateJson(Identifier id) {
        var model = new JsonObject();
        model.addProperty("model", this.addPrefix(id));
        model.addProperty("type", "minecraft:model");
        var root = new JsonObject();
        root.add("model", model);
        return root;
    }

    protected String addPrefix(Identifier id) {
        String[] s = id.toString().split(":");
        return s[0] + ":item/" + s[1];
    }

}
