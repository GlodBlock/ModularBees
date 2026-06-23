package com.glodblock.github.modularbees.common.inventory;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.util.GameUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.util.DataComponentUtil;
import org.jetbrains.annotations.NotNull;

public class MBBigItemInventory extends MBItemInventory {

    private int multiplier = 1;

    public MBBigItemInventory(BlockEntity host, int size) {
        super(host, size);
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = Math.max(multiplier, 1);
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.slotLimit * this.multiplier;
    }

    @Override
    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize() * this.multiplier);
    }

    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        var nbtTagList = new ListTag();
        for (int i = 0; i < this.stacks.size(); i++) {
            var stack = this.stacks.get(i);
            if (!stack.isEmpty()) {
                CompoundTag itemTag = (CompoundTag) DataComponentUtil.wrapEncodingExceptions(stack, GameUtil.UNLIMITED_ITEM_CODEC, provider);
                itemTag.putInt("Slot", i);
                nbtTagList.add(itemTag);
            }
        }
        var nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", this.stacks.size());
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt) {
        this.setSize(nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : this.stacks.size());
        var tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            var itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if (slot >= 0 && slot < this.stacks.size()) {
                GameUtil.UNLIMITED_ITEM_CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), itemTags)
                        .resultOrPartial(err -> ModularBees.LOGGER.error("Tried to load invalid item: '{}'", err))
                        .ifPresent(stack -> this.stacks.set(slot, stack));
            }
        }
        this.onLoad();
    }

}
