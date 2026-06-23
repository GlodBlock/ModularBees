package com.glodblock.github.modularbees.network;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.glodium.network.packet.SGenericPacket;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SMBGenericPacket extends SGenericPacket {

    public SMBGenericPacket() {
        // NO-OP
    }

    public SMBGenericPacket(String name) {
        super(name);
    }

    public SMBGenericPacket(String name, Object... paras) {
        super(name, paras);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ModularBees.id("s_generic");
    }

}
