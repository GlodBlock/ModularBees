package com.glodblock.github.modularbees.container.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class DisplaySlot extends SlotItemHandler {

    public DisplaySlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
    }

    @Override
    public @NotNull ItemStack remove(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public void set(ItemStack is) {
        if (!is.isEmpty()) {
            is = is.copy();
            is.setCount(1);
        }
        super.set(is);
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        return false;
    }

}
