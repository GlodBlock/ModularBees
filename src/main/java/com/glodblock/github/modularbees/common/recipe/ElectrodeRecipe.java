package com.glodblock.github.modularbees.common.recipe;

import com.glodblock.github.modularbees.ModularBees;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Map;

public record ElectrodeRecipe(ItemStack electrode, float power) implements Recipe<RecipeInput> {

    public static final ResourceLocation ID = ModularBees.id("overclocker_electrode");
    public static final RecipeType<ElectrodeRecipe> TYPE = RecipeType.simple(ID);
    public static final RecipeSerializer<ElectrodeRecipe> SERIALIZER = new Serializer();
    private static IdentityHashMap<Item, ElectrodeRecipe> CACHE = null;

    @Override
    public boolean matches(@NotNull RecipeInput input, @NotNull Level level) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeInput input, HolderLookup.@NotNull Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Map<Item, ElectrodeRecipe> getCache(@NotNull Level world) {
        if (CACHE == null) {
            CACHE = new IdentityHashMap<>();
            var recipes = world.getRecipeManager().byType(ElectrodeRecipe.TYPE);
            for (var recipe : recipes) {
                CACHE.put(recipe.value().electrode().getItem(), recipe.value());
            }
        }
        return CACHE;
    }

    @SubscribeEvent
    public static void onReload(OnDatapackSyncEvent event) {
        CACHE = null;
    }

    private static class Serializer implements RecipeSerializer<ElectrodeRecipe> {

        static final MapCodec<ElectrodeRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                        ItemStack.CODEC.fieldOf("electrode").forGetter(r -> r.electrode),
                        Codec.FLOAT.fieldOf("power").forGetter(r -> r.power)
                ).apply(builder, ElectrodeRecipe::new)
        );
        static final StreamCodec<RegistryFriendlyByteBuf, ElectrodeRecipe> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC,
                r -> r.electrode,
                ByteBufCodecs.FLOAT,
                r -> r.power,
                ElectrodeRecipe::new
        );

        @Override
        public @NotNull MapCodec<ElectrodeRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ElectrodeRecipe> streamCodec() {
            return STREAM_CODEC;
        }

    }

    public final static class Builder {

        ItemStack electrode;
        float power = 1;

        private Builder() {
            // NO-OP
        }

        public Builder input(ItemLike item) {
            this.electrode = new ItemStack(item);
            return this;
        }

        public Builder power(float power) {
            this.power = power;
            return this;
        }

        public void save(RecipeOutput consumer, ResourceLocation id) {
            if (this.electrode == null) {
                throw new NullPointerException("Input cannot be null! ID: %s".formatted(id));
            }
            if (!this.electrode.isDamageableItem()) {
                throw new IllegalArgumentException("Electrode must be damageable! ID: %s".formatted(id));
            }
            if (this.power < 1) {
                throw new IllegalArgumentException("Power must be greater than one! ID: %s".formatted(id));
            }
            var recipe = new ElectrodeRecipe(this.electrode, this.power);
            consumer.accept(id, recipe, null);
        }

    }
    
}
