package com.glodblock.github.modularbees.container.slot;

import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;

public class DisplaySlot extends Slot {

    private static final Container EMPTY = new SimpleContainer(0);
    private final MBItemInventory inventory;

    public DisplaySlot(MBItemInventory itemHandler, int index, int xPosition, int yPosition) {
        super(EMPTY, index, xPosition, yPosition);
        this.inventory = itemHandler;
    }

    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
    }

    @Override
    public @NotNull ItemStack remove(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack getItem() {
        return this.inventory.getItemStack(this.getSlotIndex()).copy();
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
        this.inventory.setItemStack(this.getSlotIndex(), is);
    }

    @Override
    public int getMaxStackSize() {
        return this.inventory.getCapacityAsInt(this.getSlotIndex(), ItemResource.EMPTY);
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return this.inventory.getCapacityAsInt(this.getSlotIndex(), ItemResource.of(stack));
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        return false;
    }

    @Override
    public boolean isSameInventory(@NotNull Slot other) {
        return other instanceof DisplaySlot ds && ds.inventory == this.inventory;
    }

}
