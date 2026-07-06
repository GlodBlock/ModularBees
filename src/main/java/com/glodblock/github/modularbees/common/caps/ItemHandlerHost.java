package com.glodblock.github.modularbees.common.caps;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;

public interface ItemHandlerHost {

    default ResourceHandler<@NotNull ItemResource> getItemInventory(Direction side) {
        return this.getItemInventory();
    }

    ResourceHandler<@NotNull ItemResource> getItemInventory();

}
