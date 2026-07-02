package com.glodblock.github.modularbees.xmod.jade;

import appeng.integration.modules.igtooltip.GridNodeState;
import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.xmod.ae.blocks.BlockAENetworkHost;
import com.glodblock.github.modularbees.xmod.ae.tileentities.TileAENetworkHost;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class MBJadePlugin implements IWailaPlugin {

    private static final ResourceLocation GRID_NODE_STATE = ResourceLocation.fromNamespaceAndPath(ModularBees.MODID, "grid_node");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(new ServerProvider(), TileAENetworkHost.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new ClientProvider(), BlockAENetworkHost.class);
    }

    public static class ServerProvider implements IServerDataProvider<BlockAccessor> {
        @Override
        public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            if (accessor.getBlockEntity() instanceof TileAENetworkHost host) {
                var state = GridNodeState.fromNode(host.getActionableNode());
                data.putByte("gridNodeState", (byte) state.ordinal());
            }
        }

        @Override
        public ResourceLocation getUid() {
            return GRID_NODE_STATE;
        }
    }

    public static class ClientProvider implements IBlockComponentProvider {
        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            var data = accessor.getServerData();
            if (data.contains("gridNodeState")) {
                var state = GridNodeState.values()[data.getByte("gridNodeState")];
                tooltip.add(state.textComponent().withStyle(ChatFormatting.GRAY));
            }
        }

        @Override
        public ResourceLocation getUid() {
            return GRID_NODE_STATE;
        }
    }
}
