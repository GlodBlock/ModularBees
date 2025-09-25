package com.glodblock.github.modularbees.container.slot;

import com.glodblock.github.modularbees.common.inventory.IO;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class MBInventorySlot extends SlotItemHandler {

    public MBInventorySlot(MBItemInventory itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public @NotNull MBItemInventory getItemHandler() {
        return (MBItemInventory) super.getItemHandler();
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        IO mode = this.getItemHandler().getIO(this.index);
        if (mode.canInsert() && this.getItemHandler().getFilter().valid(stack)) {
            return super.mayPlace(stack);
        }
        return false;
    }

    @Override
    public boolean mayPickup(@NotNull Player playerIn) {
        IO mode = this.getItemHandler().getIO(this.index);
        if (mode.canExtract()) {
            return super.mayPickup(playerIn);
        }
        return false;
    }

}
