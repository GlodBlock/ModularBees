package com.glodblock.github.modularbees.common.blocks.centrifuge;

import com.glodblock.github.modularbees.client.util.ConnectBlock;
import com.glodblock.github.modularbees.common.blocks.base.BlockMBTileBase;
import com.glodblock.github.modularbees.common.tileentities.centrifuge.TileCentrifugeImport;
import com.glodblock.github.modularbees.dynamic.DyResourcePack;
import com.glodblock.github.modularbees.util.RotorBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockCentrifugeImport extends BlockMBTileBase<TileCentrifugeImport> implements ConnectBlock, Centrifuge {

    public BlockCentrifugeImport() {
        super(centrifuge());
    }

    @Override
    public TagKey<Block> harvestTool() {
        return BlockTags.MINEABLE_WITH_PICKAXE;
    }

    @Override
    protected void loadBlockModel(DyResourcePack pack) {
        // NO-OP
    }

    @Override
    public boolean canConnect(BlockGetter world, BlockPos otherPos) {
        return world.getBlockState(otherPos).getBlock() instanceof Centrifuge;
    }

    @Override
    protected void onFacingChange(Direction facing, Level world, BlockPos pos) {
        var tile = this.getBlockEntity(world, pos);
        if (tile != null) {
            tile.onFacingChange();
        }
    }

    @Override
    public RotorBlocks getRotorStrategy() {
        return RotorBlocks.HORIZONTAL;
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        var state = super.defaultBlockState();
        return state.setValue(this.getRotorStrategy().property(), context.getHorizontalDirection().getOpposite());
    }

}