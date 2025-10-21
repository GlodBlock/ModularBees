package com.glodblock.github.modularbees.util;

import cy.jdkdigital.productivelib.common.recipe.TagOutputRecipe;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public interface ChanceStack {

    void get(Consumer<ItemStack> adder, RandomSource random);

    ItemStack getBaseStack();

    float getChance();

    float getAverageAmount();

    static ChanceStack of(ItemStack stack, TagOutputRecipe.ChancedOutput chancedOutput) {
        if (Mth.equal(chancedOutput.chance(), 1)) {
            if (chancedOutput.max() == chancedOutput.min()) {
                return new CommonStack(stack.copyWithCount(chancedOutput.max()));
            } else {
                return new MutableAmountStack(stack, chancedOutput.max(), chancedOutput.min());
            }
        } else {
            if (chancedOutput.max() == chancedOutput.min()) {
                return new ChanceStackImpl(stack, chancedOutput.chance());
            } else {
                return new MutableAmountChanceStack(stack, chancedOutput.max(), chancedOutput.min(), chancedOutput.chance());
            }
        }
    }

    static ChanceStack partial(ChanceStack stack, float chance) {
        return new ChanceStack() {

            @Override
            public void get(Consumer<ItemStack> adder, RandomSource random) {
                if (random.nextFloat() < chance) {
                    stack.get(adder, random);
                }
            }

            @Override
            public ItemStack getBaseStack() {
                return stack.getBaseStack();
            }

            @Override
            public float getChance() {
                return stack.getChance() * chance;
            }

            @Override
            public float getAverageAmount() {
                return stack.getAverageAmount();
            }

        };
    }

    record ChanceStackImpl(ItemStack output, float chance) implements ChanceStack {

        @Override
        public void get(Consumer<ItemStack> adder, RandomSource random) {
            if (random.nextFloat() <= this.chance) {
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

    record MutableAmountChanceStack(ItemStack output, int max, int min, float chance) implements ChanceStack {

        @Override
        public void get(Consumer<ItemStack> adder, RandomSource random) {
            if (random.nextFloat() <= this.chance) {
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

    record MutableAmountStack(ItemStack output, int max, int min) implements ChanceStack {

        @Override
        public void get(Consumer<ItemStack> adder, RandomSource random) {
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

    record CommonStack(ItemStack output) implements ChanceStack {

        @Override
        public void get(Consumer<ItemStack> adder, RandomSource random) {
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
