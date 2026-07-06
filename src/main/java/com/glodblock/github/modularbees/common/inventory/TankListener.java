package com.glodblock.github.modularbees.common.inventory;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jetbrains.annotations.NotNull;

public interface TankListener {

    void onChange(ResourceHandler<@NotNull FluidResource> inv);

}
