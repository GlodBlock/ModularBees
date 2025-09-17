package com.glodblock.github.modularbees.common.caps;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.items.IItemHandler;

public interface ItemHandlerHost {

    default IItemHandler getItemInventory(Direction side) {
        return this.getItemInventory();
    }

    IItemHandler getItemInventory();

}
