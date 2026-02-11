package com.glodblock.github.modularbees.network;

import com.glodblock.github.glodium.client.render.ColorData;
import com.glodblock.github.glodium.client.render.highlight.HighlightHandler;
import com.glodblock.github.glodium.network.packet.IMessage;
import com.glodblock.github.modularbees.ModularBees;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SMBHighlighter implements IMessage {

    static final ColorData RED = new ColorData(1f, 0f, 0f);
    BlockPos pos;
    ResourceKey<Level> level;

    public SMBHighlighter() {
        // NO-OP
    }

    public SMBHighlighter(BlockPos pos, ResourceKey<Level> level) {
        this.pos = pos;
        this.level = level;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeResourceKey(this.level);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.level = buf.readResourceKey(Registries.DIMENSION);
    }

    @Override
    public void onMessage(Player player) {
        HighlightHandler.highlight(this.pos, this.level, System.currentTimeMillis() + 5000, RED, SMBHighlighter::blink);
    }

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public ResourceLocation id() {
        return ModularBees.id("highlighter");
    }

    private static boolean blink() {
        return ((System.currentTimeMillis() / 500) & 1) != 0;
    }

}
