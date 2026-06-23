package com.glodblock.github.modularbees.common.blocks.hive;

import com.glodblock.github.modularbees.client.util.ConnectBlock;
import com.glodblock.github.modularbees.common.blocks.base.BlockMBTileBase;
import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveStacker;
import com.glodblock.github.modularbees.dynamic.DyResourcePack;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;

public class BlockBeehiveStacker extends BlockMBTileBase<TileBeehiveStacker> implements ConnectBlock, Hive {

    public BlockBeehiveStacker() {
        super(hive());
    }

    @Override
    public TagKey<Block> harvestTool() {
        return BlockTags.MINEABLE_WITH_AXE;
    }

    @Override
    protected void loadBlockModel(DyResourcePack pack) {
        // NO-OP
    }

    @Override
    public boolean canConnect(BlockGetter world, BlockPos otherPos) {
        return world.getBlockState(otherPos).getBlock() instanceof Hive;
    }

}
