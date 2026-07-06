package com.glodblock.github.modularbees.common.recipe;

import com.glodblock.github.glodium.util.GlodCodecs;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record TreaterRecipe(Ingredient input, Optional<ItemStackTemplate> output, float boost) implements NonCraftRecipe {

    private static final MapCodec<TreaterRecipe> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    Ingredient.CODEC.fieldOf("food").forGetter(r -> r.input),
                    ItemStackTemplate.CODEC.optionalFieldOf("output").forGetter(r -> r.output),
                    Codec.FLOAT.fieldOf("boost").forGetter(r -> r.boost)
            ).apply(builder, TreaterRecipe::new)
    );
    public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull TreaterRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            r -> r.input,
            GlodCodecs.optional(ItemStackTemplate.STREAM_CODEC),
            r -> r.output,
            ByteBufCodecs.FLOAT,
            r -> r.boost,
            TreaterRecipe::new
    );
    public static final Identifier ID = ModularBees.id("treater_food");
    public static final RecipeType<@NotNull TreaterRecipe> TYPE = RecipeType.simple(ID);
    public static final RecipeSerializer<@NotNull TreaterRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    @Override
    public @NotNull RecipeSerializer<@NotNull TreaterRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<@NotNull TreaterRecipe> getType() {
        return TYPE;
    }

    public boolean isValidInput(ItemStack stack) {
        return this.input.test(stack);
    }

    public static Builder builder() {
        return new Builder();
    }

    public final static class Builder {

        Ingredient input;
        ItemStackTemplate output = null;
        float boost = 1;

        private Builder() {
            // NO-OP
        }

        public Builder input(ItemLike item) {
            this.input = Ingredient.of(item);
            return this;
        }

        public Builder input(Ingredient input) {
            this.input = input;
            return this;
        }

        public Builder output(ItemLike item) {
            this.output = new ItemStackTemplate(item.asItem());
            return this;
        }

        public Builder output(ItemStackTemplate output) {
            this.output = output;
            return this;
        }

        public Builder boost(float boost) {
            this.boost = boost;
            return this;
        }

        public void save(RecipeOutput consumer, Identifier id) {
            if (this.input == null) {
                throw new NullPointerException("Input cannot be null! %s".formatted(id));
            }
            if (this.boost < 1) {
                throw new IllegalArgumentException("Boost must be greater than one! %s".formatted(id));
            }
            var recipe = new TreaterRecipe(this.input, Optional.ofNullable(this.output), this.boost);
            consumer.accept(ResourceKey.create(Registries.RECIPE, id), recipe, null);
        }

    }

}
