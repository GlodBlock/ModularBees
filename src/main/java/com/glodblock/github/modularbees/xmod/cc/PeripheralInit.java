package com.glodblock.github.modularbees.xmod.cc;

import dan200.computercraft.api.ComputerCraftAPI;

public class PeripheralInit {

    public static void init() {
        ComputerCraftAPI.registerGenericSource(new HivePeripheral());
    }

}
