package com.glodblock.github.modularbees.common.inventory;

import com.glodblock.github.modularbees.common.tileentities.base.TileMBBase;
import com.glodblock.github.modularbees.util.ResourceHandlerAccessor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class MBItemInventory extends ItemStacksResourceHandler {

    protected final IO[] mode;
    protected final BlockEntity host;
    protected final ItemFilter[] filter;
    protected int slotLimit = Item.ABSOLUTE_MAX_STACK_SIZE;

    public MBItemInventory(BlockEntity host, int size) {
        super(size);
        this.host = host;
        this.mode = new IO[size];
        this.filter = new ItemFilter[size];
        Arrays.fill(this.mode, IO.ALL);
        Arrays.fill(this.filter, ItemFilter.PASS);
    }

    public MBItemInventory(BlockEntity host, int size, ItemFilter filter) {
        super(size);
        this.host = host;
        this.filter = new ItemFilter[size];
        this.mode = new IO[size];
        Arrays.fill(this.mode, IO.ALL);
        Arrays.fill(this.filter, filter);
    }

    public IO getIO(int slot) {
        return this.mode[slot];
    }

    public ItemFilter getFilter(int slot) {
        return this.filter[slot];
    }

    public MBItemInventory setSlotLimit(int limit) {
        this.slotLimit = limit;
        return this;
    }

    public MBItemInventory setFilter(ItemFilter filter) {
        Arrays.fill(this.filter, filter);
        return this;
    }

    public MBItemInventory setFilter(ItemFilter filter, int slot) {
        this.filter[slot] = filter;
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
    public int insert(int slot, @NotNull ItemResource item, int amount, @NotNull TransactionContext transaction) {
        if (this.mode[slot].canInsert() && this.filter[slot].valid(item)) {
            return super.insert(slot, item, amount, transaction);
        } else {
            return 0;
        }
    }

    @Override
    public int extract(int slot, @NotNull ItemResource item, int amount, @NotNull TransactionContext transaction) {
        if (this.mode[slot].canExtract()) {
            return super.extract(slot, item, amount, transaction);
        } else {
            return 0;
        }
    }

    public int forceInsert(int slot, @NotNull ItemResource item, int amount, @NotNull TransactionContext transaction) {
        return super.insert(slot, item, amount, transaction);
    }

    public int forceInsert(@NotNull ItemResource resource, int amount, @NotNull TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        int inserted = 0;
        for (int index = 0; index < this.size(); index++) {
            inserted += super.insert(index, resource, amount - inserted, transaction);
            if (inserted == amount) break;
        }
        return inserted;
    }

    public int forceExtract(int slot, @NotNull ItemResource item, int amount, @NotNull TransactionContext transaction) {
        return super.extract(slot, item, amount, transaction);
    }

    public int forceExtract(@NotNull ItemResource resource, int amount, @NotNull TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        int extracted = 0;
        for (int index = 0; index < this.size(); index++) {
            extracted += super.extract(index, resource, amount - extracted, transaction);
            if (extracted == amount) break;
        }
        return extracted;
    }

    @Override
    protected void onContentsChanged(int slot, ItemStack stack) {
        if (this.host != null) {
            if (this.host instanceof SlotListener listener) {
                listener.onChange(this, slot);
            }
            this.markDirty();
        }
    }

    @Override
    public int getCapacity(int index, @NotNull ItemResource item) {
        return item.isEmpty() ? this.slotLimit : Math.min(this.slotLimit, item.getMaxStackSize());
    }

    public List<ItemStack> toList() {
        return List.copyOf(this.stacks);
    }

    public boolean contains(Predicate<ItemStack> filter) {
        for (var stack : this.stacks) {
            if (filter.test(stack)) {
                return true;
            }
        }
        return false;
    }

    public int countStack(Item filter) {
        int cnt = 0;
        for (var stack : this.stacks) {
            if (filter == stack.getItem()) {
                cnt += stack.getCount();
            }
        }
        return cnt;
    }

    public ItemStack getItemStack(int slot) {
        return this.stacks.get(slot);
    }

    public void setItemStack(int slot, ItemStack stack) {
        var old = this.stacks.get(slot);
        this.stacks.set(slot, stack);
        if (!ItemStack.isSameItemSameComponents(old, stack)) {
            this.onContentsChanged(slot, stack);
        }
    }

    public ResourceHandlerAccessor accessor() {
        return (ResourceHandlerAccessor) this;
    }

    private void markDirty() {
        if (this.host instanceof TileMBBase base) {
            base.markDirty();
        } else {
            this.host.setChanged();
        }
    }

    public interface ItemFilter {

        ItemFilter PASS = _ -> true;

        boolean valid(ItemResource stack);

        static ItemFilter of(Item stack) {
            return input -> input.is(stack);
        }

        static ItemFilter of(Predicate<ItemStack> filter) {
            return type -> filter.test(type.toStack());
        }

    }

}
