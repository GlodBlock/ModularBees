package com.glodblock.github.modularbees.common.inventory;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;

public interface SlotListener {

    void onChange(ResourceHandler<@NotNull ItemResource> inv, int slot);

}
