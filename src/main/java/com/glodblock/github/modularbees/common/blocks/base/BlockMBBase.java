package com.glodblock.github.modularbees.common.blocks.base;

import com.glodblock.github.modularbees.common.items.ItemMBBlock;
import com.glodblock.github.modularbees.dynamic.DyDataPack;
import com.glodblock.github.modularbees.dynamic.DyResourcePack;
import com.glodblock.github.modularbees.util.DataProvider;
import com.glodblock.github.modularbees.util.RegisterTask;
import com.glodblock.github.modularbees.util.ResourceProvider;
import com.glodblock.github.modularbees.util.RotorBlocks;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockMBBase extends Block implements RegisterTask, ResourceProvider, DataProvider {

    private ResourceLocation registryName;

    public BlockMBBase(Properties properties) {
        super(properties);
        if (!this.getRotorStrategy().isNone()) {
            this.registerDefaultState(this.defaultBlockState().setValue(this.getRotorStrategy().property(), this.getRotorStrategy().defaultFace()));
        }
    }

    @Override
    public @NotNull String toString() {
        String regName = this.getRegistryName() != null ? this.getRegistryName().getPath() : "unregistered";
        return this.getClass().getSimpleName() + "[" + regName + "]";
    }

    public RotorBlocks getRotorStrategy() {
        return RotorBlocks.NONE;
    }

    public Item createItem() {
        return new ItemMBBlock(this, new Item.Properties());
    }

    @NotNull
    public final Direction[] getValidFaces() {
        return this.getRotorStrategy().isNone() ? new Direction[0] : this.getRotorStrategy().faces();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        if (!this.getRotorStrategy().isNone()) {
            builder.add(this.getRotorStrategy().property());
        }
    }

    @Nullable
    public TagKey<Block> harvestTool() {
        return null;
    }

    public Direction getFacing(BlockState state) {
        if (!this.getRotorStrategy().isNone()) {
            return state.getValue(this.getRotorStrategy().property());
        }
        return null;
    }

    @Override
    public void onRegister(ResourceLocation id) {
        this.registryName = id;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public static BlockBehaviour.Properties hive() {
        return BlockBehaviour.Properties.of()
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.2f)
                .mapColor(MapColor.WOOD)
                .sound(SoundType.WOOD);
    }

    @Override
    public void load(DyDataPack pack) {
        this.loadLootTable(pack);
        this.loadBlockTag(pack);
    }

    protected void loadLootTable(DyDataPack pack) {
        pack.addBlockLootTable(this.registryName, createSingleBlockLootJson(this.registryName));
    }

    protected void loadBlockTag(DyDataPack pack) {
        if (this.harvestTool() != null) {
            pack.addBlockTag(this.harvestTool(), this.registryName);
        }
    }

    @Override
    public void load(DyResourcePack pack) {
        this.loadBlockState(pack);
        this.loadBlockModel(pack);
        this.loadBlockItemModel(pack);
    }

    protected void loadBlockState(DyResourcePack pack) {
        pack.addBlockState(this.registryName, createStateJson(this.registryName));
    }

    // Default full cube block
    protected void loadBlockModel(DyResourcePack pack) {
        pack.addBlockModel(this.registryName, createFullCubeModelJson(this.registryName));
    }

    protected void loadBlockItemModel(DyResourcePack pack) {
        pack.addItemModel(this.registryName, createItemModelJson(this.registryName));
    }

    protected JsonObject createSingleBlockLootJson(ResourceLocation id) {
        var root = new JsonObject();
        root.addProperty("type", "minecraft:block");
        {
            var pools = new JsonArray();
            {
                var pool = new JsonObject();
                pool.addProperty("bonus_rolls", 0.0);
                {
                    var condition = new JsonObject();
                    condition.addProperty("condition", "minecraft:survives_explosion");
                    var conditions = new JsonArray();
                    conditions.add(condition);
                    pool.add("conditions", conditions);
                    var entry = new JsonObject();
                    entry.addProperty("type", "minecraft:item");
                    entry.addProperty("name", id.toString());
                    var entries = new JsonArray();
                    entries.add(entry);
                    pool.add("entries", entries);
                }
                pool.addProperty("rolls", 1.0);
                pools.add(pool);
            }
            root.add("pools", pools);
        }
        root.addProperty("random_sequence", this.addPrefix(id));
        return root;
    }

    protected JsonObject createFullCubeModelJson(ResourceLocation id) {
        var texture = new JsonObject();
        texture.addProperty("all", this.addPrefix(id));
        var root = new JsonObject();
        root.add("textures", texture);
        root.addProperty("parent", "minecraft:block/cube_all");
        return root;
    }

    protected JsonObject createStateJson(ResourceLocation id) {
        var variants = new JsonObject();
        for (var face : this.getValidFaces()) {
            var sub = new JsonObject();
            sub.addProperty("model", this.addPrefix(id));
            switch (face) {
                case UP -> this.modifyModel(270, 0, sub);
                case DOWN -> this.modifyModel(90, 0, sub);
                case NORTH -> this.modifyModel(0, 0, sub);
                case SOUTH -> this.modifyModel(0, 180, sub);
                case EAST -> this.modifyModel(0, 90, sub);
                case WEST -> this.modifyModel(0, 270, sub);
            }
            variants.add("facing=" + face.getName(), sub);
        }
        if (this.getValidFaces().length == 0) {
            var sub = new JsonObject();
            sub.addProperty("model", this.addPrefix(id));
            variants.add("", sub);
        }
        var root = new JsonObject();
        root.add("variants", variants);
        return root;
    }

    protected void modifyModel(int x, int y, JsonObject json) {
        if (x != 0) {
            json.addProperty("x", x);
        }
        if (y != 0) {
            json.addProperty("y", y);
        }
    }

    protected String addPrefix(ResourceLocation id) {
        String[] s = id.toString().split(":");
        return s[0] + ":block/" + s[1];
    }

    protected JsonObject createItemModelJson(ResourceLocation id) {
        var parent = new JsonObject();
        parent.addProperty("parent", this.addPrefix(id));
        return parent;
    }

}
