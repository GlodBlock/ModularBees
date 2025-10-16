package com.glodblock.github.modularbees.common.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class MBItemInventory extends ItemStackHandler {

    protected final IO[] mode;
    protected final BlockEntity host;
    protected ItemFilter filter = ItemFilter.PASS;
    protected int slotLimit = Item.ABSOLUTE_MAX_STACK_SIZE;

    public MBItemInventory(BlockEntity host, int size) {
        super(size);
        this.host = host;
        this.mode = new IO[size];
        Arrays.fill(this.mode, IO.ALL);
    }

    public MBItemInventory(BlockEntity host, int size, ItemFilter filter) {
        super(size);
        this.host = host;
        this.filter = filter;
        this.mode = new IO[size];
        Arrays.fill(this.mode, IO.ALL);
    }

    public IO getIO(int slot) {
        return this.mode[slot];
    }

    public ItemFilter getFilter() {
        return this.filter;
    }

    public MBItemInventory setSlotLimit(int limit) {
        this.slotLimit = limit;
        return this;
    }

    public MBItemInventory setFilter(ItemFilter filter) {
        this.filter = filter;
        return this;
    }

    public MBItemInventory setIO(int slot, IO mode) {
        this.mode[slot] = mode;
        return this;
    }

    public MBItemInventory inputOnly() {
        Arrays.fill(this.mode, IO.IN);
        return this;
    }

    public MBItemInventory outputOnly() {
        Arrays.fill(this.mode, IO.OUT);
        return this;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (this.mode[slot].canInsert() && this.filter.valid(stack)) {
            return super.insertItem(slot, stack, simulate);
        } else {
            return stack;
        }
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (this.mode[slot].canExtract()) {
            return super.extractItem(slot, amount, simulate);
        } else {
            return ItemStack.EMPTY;
        }
    }

    public @NotNull ItemStack forceInsertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return super.insertItem(slot, stack, simulate);
    }

    public @NotNull ItemStack forceExtractItem(int slot, int amount, boolean simulate) {
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (this.host != null) {
            if (this.host instanceof SlotListener listener) {
                listener.onChange(this, slot);
            }
            this.host.setChanged();
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.slotLimit;
    }

    @Override
    public void setSize(int size) {
        if (size == this.stacks.size()) {
            return;
        }
        var newStack = NonNullList.withSize(size, ItemStack.EMPTY);
        int bound = Math.min(this.stacks.size(), size);
        for (int i = 0; i < bound; i++) {
            newStack.set(i, this.stacks.get(i));
        }
        this.stacks = newStack;
    }

    public List<ItemStack> toList() {
        return List.copyOf(this.stacks);
    }

    public int countStack(Predicate<ItemStack> filter) {
        int cnt = 0;
        for (var stack : this.stacks) {
            if (filter.test(stack)) {
                cnt ++;
            }
        }
        return cnt;
    }

    public int countStack(Item filter) {
        int cnt = 0;
        for (var stack : this.stacks) {
            if (filter == stack.getItem()) {
                cnt ++;
            }
        }
        return cnt;
    }

    public boolean hasItem() {
        for (var stack : this.stacks) {
            if (!stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public interface ItemFilter {

        ItemFilter PASS = s -> true;

        boolean valid(ItemStack stack);

        static ItemFilter of(Item stack) {
            return input -> input.is(stack);
        }

    }

}
