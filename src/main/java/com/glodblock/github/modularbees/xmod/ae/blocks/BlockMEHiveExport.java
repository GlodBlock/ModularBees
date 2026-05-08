package com.glodblock.github.modularbees.xmod.ae.blocks;

import com.glodblock.github.modularbees.client.util.ConnectBlock;
import com.glodblock.github.modularbees.common.blocks.hive.Hive;
import com.glodblock.github.modularbees.container.base.MBGuiHandler;
import com.glodblock.github.modularbees.dynamic.DyResourcePack;
import com.glodblock.github.modularbees.util.ContainerResolver;
import com.glodblock.github.modularbees.util.RotorBlocks;
import com.glodblock.github.modularbees.xmod.ae.container.ContainerMEExport;
import com.glodblock.github.modularbees.xmod.ae.tileentities.TileMEHiveExport;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockMEHiveExport extends BlockAENetworkHost<TileMEHiveExport> implements ConnectBlock, Hive {

    public BlockMEHiveExport() {
        super(hive());
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

    @Override
    public TagKey<Block> harvestTool() {
        return BlockTags.MINEABLE_WITH_AXE;
    }

    @Override
    protected void loadBlockModel(DyResourcePack pack) {
        // NO-OP
    }

    @Override
    public void openGui(TileMEHiveExport tile, Player p) {
        MBGuiHandler.open(ContainerMEExport.TYPE.type(), p, ContainerResolver.of(tile));
    }

    @Override
    public boolean canConnect(BlockGetter world, BlockPos otherPos) {
        return world.getBlockState(otherPos).getBlock() instanceof Hive;
    }

}
