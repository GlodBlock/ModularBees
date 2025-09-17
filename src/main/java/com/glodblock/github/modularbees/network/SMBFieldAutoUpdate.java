package com.glodblock.github.modularbees.network;

import com.glodblock.github.glodium.network.packet.IMessage;
import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.container.ContainerMBBase;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.connection.ConnectionType;

import java.util.Arrays;
import java.util.function.Consumer;

public class SMBFieldAutoUpdate implements IMessage {

    Consumer<RegistryFriendlyByteBuf> writer;
    byte[] raw;
    int id;

    public SMBFieldAutoUpdate() {
        // NO-OP
    }

    public SMBFieldAutoUpdate(Consumer<RegistryFriendlyByteBuf> writer, int id) {
        this.writer = writer;
        this.id = id;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.id);
        this.writer.accept(buf);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        this.id = buf.readInt();
        this.raw = new byte[buf.readableBytes()];
        buf.readBytes(this.raw);
    }

    @Override
    public void onMessage(Player player) {
        var container = player.containerMenu;
        if (container.containerId == this.id && container instanceof ContainerMBBase<?> receiver) {
            receiver.receiveUpdate(new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(this.raw), player.registryAccess(), ConnectionType.NEOFORGE));
        }
    }

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public ResourceLocation id() {
        return ModularBees.id("field_auto_sync");
    }

}
