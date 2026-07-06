package com.glodblock.github.modularbees.common.recipe;

import com.glodblock.github.modularbees.ModularBees;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Map;

public record ElectrodeRecipe(ItemStackTemplate electrode, float power) implements NonCraftRecipe {

    private static final MapCodec<ElectrodeRecipe> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    ItemStackTemplate.CODEC.fieldOf("electrode").forGetter(r -> r.electrode),
                    Codec.FLOAT.fieldOf("power").forGetter(r -> r.power)
            ).apply(builder, ElectrodeRecipe::new)
    );
    private static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull ElectrodeRecipe> STREAM_CODEC = StreamCodec.composite(
            ItemStackTemplate.STREAM_CODEC,
            r -> r.electrode,
            ByteBufCodecs.FLOAT,
            r -> r.power,
            ElectrodeRecipe::new
    );
    private static IdentityHashMap<Item, ElectrodeRecipe> CACHE = null;
    public static final Identifier ID = ModularBees.id("overclocker_electrode");
    public static final RecipeType<@NotNull ElectrodeRecipe> TYPE = RecipeType.simple(ID);
    public static final RecipeSerializer<@NotNull ElectrodeRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    @Override
    public @NotNull RecipeSerializer<@NotNull ElectrodeRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<@NotNull ElectrodeRecipe> getType() {
        return TYPE;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Map<Item, ElectrodeRecipe> getCache(@NotNull ServerLevel world) {
        if (CACHE == null) {
            CACHE = new IdentityHashMap<>();
            var recipes = world.recipeAccess().recipeMap().byType(ElectrodeRecipe.TYPE);
            for (var recipe : recipes) {
                CACHE.put(recipe.value().electrode().item().value(), recipe.value());
            }
        }
        return CACHE;
    }

    @SubscribeEvent
    public static void onReload(OnDatapackSyncEvent event) {
        CACHE = null;
    }

    public final static class Builder {

        ItemStackTemplate electrode;
        float power = 1;

        private Builder() {
            // NO-OP
        }

        public Builder input(ItemLike item) {
            this.electrode = new ItemStackTemplate(item.asItem());
            return this;
        }

        public Builder power(float power) {
            this.power = power;
            return this;
        }

        public void save(RecipeOutput consumer, Identifier id) {
            if (this.electrode == null) {
                throw new NullPointerException("Input cannot be null! ID: %s".formatted(id));
            }
            if (this.power < 1) {
                throw new IllegalArgumentException("Power must be greater than one! ID: %s".formatted(id));
            }
            var recipe = new ElectrodeRecipe(this.electrode, this.power);
            consumer.accept(ResourceKey.create(Registries.RECIPE, id), recipe, null);
        }

    }
    
}
