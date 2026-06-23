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
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record TreaterRecipe(Ingredient input, ItemStack output, float boost) implements Recipe<RecipeInput> {

    public static final ResourceLocation ID = ModularBees.id("treater_food");
    public static final RecipeType<TreaterRecipe> TYPE = RecipeType.simple(ID);
    public static final RecipeSerializer<TreaterRecipe> SERIALIZER = new Serializer();

    @Override
    public boolean matches(@NotNull RecipeInput input, @NotNull Level world) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeInput input, HolderLookup.@NotNull Provider provider) {
        return this.getResultItem(provider).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider provider) {
        return this.output.copy();
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

    @Override
    public @NotNull String getGroup() {
        return "treater";
    }

    public boolean isValidInput(ItemStack stack) {
        return this.input.test(stack);
    }

    public static Builder builder() {
        return new Builder();
    }

    private static class Serializer implements RecipeSerializer<TreaterRecipe> {

        static final MapCodec<TreaterRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                        Ingredient.CODEC.fieldOf("food").forGetter(r -> r.input),
                        ItemStack.OPTIONAL_CODEC.fieldOf("output").forGetter(r -> r.output),
                        Codec.FLOAT.fieldOf("boost").forGetter(r -> r.boost)
                ).apply(builder, TreaterRecipe::new)
        );
        static final StreamCodec<RegistryFriendlyByteBuf, TreaterRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC,
                r -> r.input,
                ItemStack.OPTIONAL_STREAM_CODEC,
                r -> r.output,
                ByteBufCodecs.FLOAT,
                r -> r.boost,
                TreaterRecipe::new
        );

        @Override
        public @NotNull MapCodec<TreaterRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, TreaterRecipe> streamCodec() {
            return STREAM_CODEC;
        }

    }

    public final static class Builder {

        Ingredient input;
        ItemStack output = ItemStack.EMPTY;
        float boost = 1;

        private Builder() {
            // NO-OP
        }

        public Builder input(ItemLike item) {
            this.input = Ingredient.of(item);
            return this;
        }

        public Builder input(TagKey<Item> tag) {
            this.input = Ingredient.of(tag);
            return this;
        }

        public Builder input(Ingredient input) {
            this.input = input;
            return this;
        }

        public Builder output(ItemLike item) {
            this.output = new ItemStack(item);
            return this;
        }

        public Builder output(ItemStack output) {
            this.output = output;
            return this;
        }

        public Builder boost(float boost) {
            this.boost = boost;
            return this;
        }

        public void save(RecipeOutput consumer, ResourceLocation id) {
            if (this.input == null || this.output == null) {
                throw new NullPointerException("Input and output cannot be null! %s".formatted(id));
            }
            if (this.boost < 1) {
                throw new IllegalArgumentException("Boost must be greater than one! %s".formatted(id));
            }
            var recipe = new TreaterRecipe(this.input, this.output, this.boost);
            consumer.accept(id, recipe, null);
        }

    }

}
