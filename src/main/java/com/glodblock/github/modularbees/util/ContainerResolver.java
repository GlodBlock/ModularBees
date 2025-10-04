package com.glodblock.github.modularbees.util;

import com.glodblock.github.modularbees.container.base.MBGuiHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ContainerResolver {

    <T> T resolve(Player player, Class<T> type);

    void fromBytes(RegistryFriendlyByteBuf buf);

    void toBytes(RegistryFriendlyByteBuf buf);

    static ContainerResolver of(BlockPos pos) {
        return new MBGuiHandler.TileResolver(pos);
    }

    static ContainerResolver of(BlockEntity tile) {
        return new MBGuiHandler.TileResolver(tile.getBlockPos());
    }

}
