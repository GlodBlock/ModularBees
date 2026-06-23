package com.glodblock.github.modularbees.xmod.ae.expose;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public interface MEExportAction {

    void sendToMENetwork(List<ItemStack> sending);

    void sendToMENetworkFluid(List<FluidStack> filling);

}
