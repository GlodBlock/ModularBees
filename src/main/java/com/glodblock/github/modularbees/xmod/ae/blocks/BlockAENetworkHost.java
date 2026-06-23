package com.glodblock.github.modularbees.xmod.ae.blocks;

import com.glodblock.github.modularbees.common.blocks.base.BlockMBGuiBase;
import com.glodblock.github.modularbees.xmod.ae.tileentities.TileAENetworkHost;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public abstract class BlockAENetworkHost<T extends TileAENetworkHost> extends BlockMBGuiBase<T> {

    public BlockAENetworkHost(Properties properties) {
        super(properties);
    }

    @Override
    protected void onFacingChange(Direction facing, Level world, BlockPos pos) {
        var tile = this.getBlockEntity(world, pos);
        if (tile != null) {
            tile.invalidateCapabilities();
            tile.onGridConnectableSidesChanged();
        }
    }

    @Override
    public boolean isOptionalBlock() {
        return true;
    }

}
