package com.glodblock.github.modularbees.common.tileentities.centrifuge;

import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.util.ServerTickTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

public class TileCentrifugeImport extends TileCentrifugePart implements ServerTickTile, ItemHandlerHost {

    public TileCentrifugeImport(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public void onFacingChange() {
        this.invalidateCapabilities();
    }

    @Override
    public IItemHandler getItemInventory() {
        return null;
    }

    @Override
    public void tickServer(Level world, BlockState state) {

    }

}
