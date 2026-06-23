package com.glodblock.github.modularbees.common.inventory;

import net.neoforged.neoforge.items.IItemHandler;

public interface SlotListener {

    void onChange(IItemHandler inv, int slot);

}
