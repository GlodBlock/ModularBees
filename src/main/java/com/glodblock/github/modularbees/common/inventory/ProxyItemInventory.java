package com.glodblock.github.modularbees.common.inventory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public record ProxyItemInventory(Supplier<IItemHandler> getter) implements IItemHandler {

    @Override
    public int getSlots() {
        return this.getter.get().getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return this.getter.get().getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return this.getter.get().insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.getter.get().extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.getter.get().getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return this.getter.get().isItemValid(slot, stack);
    }

}