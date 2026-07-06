package com.glodblock.github.modularbees.common.inventory;

import com.glodblock.github.modularbees.util.GameUtil;
import com.mojang.serialization.Codec;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;

public class MBBigItemInventory extends MBItemInventory {

    public static final Codec<NonNullList<@NotNull ItemStack>> BIG_CODEC = GameUtil.UNLIMITED_ITEM_CODEC.listOf().xmap(MBBigItemInventory::mutableCopyOf, Function.identity());
    private int multiplier = 1;

    public MBBigItemInventory(BlockEntity host, int size) {
        super(host, size);
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = Math.max(multiplier, 1);
    }

    public static NonNullList<@NotNull ItemStack> mutableCopyOf(Collection<ItemStack> list) {
        return NonNullList.of(ItemStack.EMPTY, (ItemStack[]) list.toArray(Object[]::new));
    }

    @Override
    public int getCapacity(int slot, @NotNull ItemResource item) {
        return item.isEmpty() ? this.slotLimit * this.multiplier : Math.min(this.slotLimit * this.multiplier, item.getMaxStackSize() * this.multiplier);
    }

    @Override
    public void serialize(@NotNull ValueOutput output) {
        output.store(VALUE_IO_KEY, BIG_CODEC, this.stacks);
    }

    @Override
    public void deserialize(@NotNull ValueInput input) {
        input.read(VALUE_IO_KEY, BIG_CODEC).ifPresent(l -> {
            stacks = l;
            this.accessor().updateStacksSize();
        });
    }

}
