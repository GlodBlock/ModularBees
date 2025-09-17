package com.glodblock.github.modularbees.network;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.glodium.network.NetworkHandler;

public class MBNetworkHandler extends NetworkHandler {

    public static final MBNetworkHandler INSTANCE = new MBNetworkHandler();

    public MBNetworkHandler() {
        super(ModularBees.MODID);
        registerPacket(SMBGenericPacket::new);
        registerPacket(CMBGenericPacket::new);
        registerPacket(SMBFieldAutoUpdate::new);
    }

}
