package com.glodblock.github.modularbees.container.slot;

import com.glodblock.github.modularbees.common.inventory.IO;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

public class MBInventorySlot extends ResourceHandlerSlot {

    public MBInventorySlot(MBItemInventory itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, itemHandler::set, index, xPosition, yPosition);
    }

    @Override
    public @NotNull MBItemInventory getResourceHandler() {
        return (MBItemInventory) super.getResourceHandler();
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        IO mode = this.getResourceHandler().getIO(this.getSlotIndex());
        MBItemInventory.ItemFilter filter = this.getResourceHandler().getFilter(this.getSlotIndex());
        if (mode.canInsert() && filter.valid(ItemResource.of(stack))) {
            return super.mayPlace(stack);
        }
        return false;
    }

    // Always allow to extract items in GUI
    @Override
    public boolean mayPickup(@NotNull Player playerIn) {
        var resource = this.getResourceHandler().getResource(this.getSlotIndex());
        if (resource.isEmpty()) {
            return false;
        }
        try (var tx = Transaction.openRoot()) {
            return this.getResourceHandler().forceExtract(this.getSlotIndex(), resource, 1, tx) == 1;
        }
    }

}
