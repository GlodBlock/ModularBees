package com.glodblock.github.modularbees.xmod.ae;

import appeng.api.integrations.igtooltip.BaseClassRegistration;
import appeng.api.integrations.igtooltip.TooltipProvider;
import com.glodblock.github.modularbees.xmod.ae.blocks.BlockAENetworkHost;
import com.glodblock.github.modularbees.xmod.ae.tileentities.TileAENetworkHost;

@SuppressWarnings("UnstableApiUsage")
public class AEXTooltip implements TooltipProvider {

    @Override
    public void registerBlockEntityBaseClasses(BaseClassRegistration registration) {
        registration.addBaseBlockEntity(TileAENetworkHost.class, BlockAENetworkHost.class);
    }

}
