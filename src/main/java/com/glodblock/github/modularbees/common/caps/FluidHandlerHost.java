package com.glodblock.github.modularbees.common.caps;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jetbrains.annotations.NotNull;

public interface FluidHandlerHost {

    default ResourceHandler<@NotNull FluidResource> getFluidInventory(Direction side) {
        return this.getFluidInventory();
    }

    ResourceHandler<@NotNull FluidResource> getFluidInventory();

}
