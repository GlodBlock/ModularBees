package com.glodblock.github.modularbees.xmod.ae;

import appeng.api.integrations.igtooltip.ClientRegistration;
import appeng.api.integrations.igtooltip.CommonRegistration;
import appeng.api.integrations.igtooltip.TooltipProvider;
import appeng.integration.modules.igtooltip.blocks.GridNodeStateDataProvider;
import appeng.integration.modules.igtooltip.blocks.PowerStorageDataProvider;
import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.xmod.ae.blocks.BlockAENetworkHost;
import com.glodblock.github.modularbees.xmod.ae.tileentities.TileAENetworkHost;

@SuppressWarnings("UnstableApiUsage")
public class AEXTooltip implements TooltipProvider {

    @Override
    public void registerClient(ClientRegistration registration) {
        registration.addBlockEntityBody(TileAENetworkHost.class, BlockAENetworkHost.class, ModularBees.id("power_storage"), new PowerStorageDataProvider());
        registration.addBlockEntityBody(TileAENetworkHost.class, BlockAENetworkHost.class, ModularBees.id("grid_node_state"), new GridNodeStateDataProvider());
    }

    @Override
    public void registerCommon(CommonRegistration registration) {
        registration.addBlockEntityData(ModularBees.id("grid_node"), TileAENetworkHost.class, new GridNodeStateDataProvider());
        registration.addBlockEntityData(ModularBees.id("power_storage"), TileAENetworkHost.class, new PowerStorageDataProvider());
    }

}
