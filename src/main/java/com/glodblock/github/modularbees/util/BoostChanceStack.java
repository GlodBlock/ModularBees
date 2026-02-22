package com.glodblock.github.modularbees.util;

import cy.jdkdigital.productivelib.common.recipe.TagOutputRecipe;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public interface BoostChanceStack {

    void get(Consumer<ItemStack> adder, float boost, RandomSource random);

    ItemStack getBaseStack();

    float getChance();

    float getAverageAmount();

    static BoostChanceStack of(ItemStack stack, TagOutputRecipe.ChancedOutput chancedOutput) {
        if (Mth.equal(chancedOutput.chance(), 1)) {
            if (chancedOutput.max() == chancedOutput.min()) {
                return new CommonStack(stack.copyWithCount(chancedOutput.max()));
            } else {
                return new MutableAmountStack(stack, chancedOutput.max(), chancedOutput.min());
            }
        } else {
            if (chancedOutput.max() == chancedOutput.min()) {
                return new BoostChanceStackImpl(stack.copyWithCount(chancedOutput.max()), chancedOutput.chance());
            } else {
                return new MutableAmountBoostChanceStack(stack, chancedOutput.max(), chancedOutput.min(), chancedOutput.chance());
            }
        }
    }

    record BoostChanceStackImpl(ItemStack output, float chance) implements BoostChanceStack {

        @Override
        public void get(Consumer<ItemStack> adder, float boost, RandomSource random) {
            if (random.nextFloat() <= this.chance + boost) {
                adder.accept(this.output.copy());
            }
        }

        @Override
        public ItemStack getBaseStack() {
            return this.output;
        }

        @Override
        public float getChance() {
            return this.chance;
        }

        @Override
        public float getAverageAmount() {
            return this.output.getCount();
        }

    }

    record MutableAmountBoostChanceStack(ItemStack output, int max, int min, float chance) implements BoostChanceStack {

        @Override
        public void get(Consumer<ItemStack> adder, float boost, RandomSource random) {
            if (random.nextFloat() <= this.chance + boost) {
                int amt = Mth.nextInt(random, this.min, this.max);
                adder.accept(this.output.copyWithCount(amt));
            }
        }

        @Override
        public ItemStack getBaseStack() {
            return this.output;
        }

        @Override
        public float getChance() {
            return this.chance;
        }

        @Override
        public float getAverageAmount() {
            return (this.max + this.min) / 2.0F;
        }

    }

    record MutableAmountStack(ItemStack output, int max, int min) implements BoostChanceStack {

        @Override
        public void get(Consumer<ItemStack> adder, float boost, RandomSource random) {
            int amt = Mth.nextInt(random, this.min, this.max);
            adder.accept(this.output.copyWithCount(amt));
        }

        @Override
        public ItemStack getBaseStack() {
            return this.output;
        }

        @Override
        public float getChance() {
            return 1;
        }

        @Override
        public float getAverageAmount() {
            return (this.max + this.min) / 2.0F;
        }

    }

    record CommonStack(ItemStack output) implements BoostChanceStack {

        @Override
        public void get(Consumer<ItemStack> adder, float boost, RandomSource random) {
            adder.accept(this.output.copy());
        }

        @Override
        public ItemStack getBaseStack() {
            return this.output;
        }

        @Override
        public float getChance() {
            return 1;
        }

        @Override
        public float getAverageAmount() {
            return this.output.getCount();
        }

    }
    
}
