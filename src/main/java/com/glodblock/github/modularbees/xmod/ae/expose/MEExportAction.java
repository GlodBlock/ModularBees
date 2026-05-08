package com.glodblock.github.modularbees.xmod.ae.expose;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface MEExportAction {

    void sendToMENetwork(List<ItemStack> sending);

}
