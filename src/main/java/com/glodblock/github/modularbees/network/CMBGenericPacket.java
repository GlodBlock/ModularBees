package com.glodblock.github.modularbees.network;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.glodium.network.packet.CGenericPacket;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CMBGenericPacket extends CGenericPacket {

    public CMBGenericPacket() {
        // NO-OP
    }

    public CMBGenericPacket(String name) {
        super(name);
    }

    public CMBGenericPacket(String name, Object... paras) {
        super(name, paras);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ModularBees.id("c_generic");
    }
}
