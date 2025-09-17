package com.glodblock.github.modularbees.common.blocks.hive;

import com.glodblock.github.modularbees.client.util.ConnectBlock;
import com.glodblock.github.modularbees.common.blocks.base.BlockMBGuiBase;
import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveAlveary;
import com.glodblock.github.modularbees.container.ContainerMBAlveary;
import com.glodblock.github.modularbees.container.MBGuiHandler;
import com.glodblock.github.modularbees.dynamic.DyResourcePack;
import com.glodblock.github.modularbees.util.ContainerResolver;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;

public class BlockBeehiveAlveary extends BlockMBGuiBase<TileBeehiveAlveary> implements ConnectBlock, Hive {

    public BlockBeehiveAlveary() {
        super(hive());
    }

    @Override
    public void openGui(TileBeehiveAlveary tile, Player p) {
        MBGuiHandler.open(ContainerMBAlveary.TYPE.type(), p, ContainerResolver.of(tile));
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
