package com.glodblock.github.modularbees.common.blocks.hive;

import com.glodblock.github.modularbees.client.util.ConnectBlock;
import com.glodblock.github.modularbees.common.blocks.base.BlockMBGuiBase;
import com.glodblock.github.modularbees.common.tileentities.hive.TileModularBeehive;
import com.glodblock.github.modularbees.container.ContainerMBModularBeehive;
import com.glodblock.github.modularbees.container.MBGuiHandler;
import com.glodblock.github.modularbees.dynamic.DyResourcePack;
import com.glodblock.github.modularbees.util.ContainerResolver;
import com.glodblock.github.modularbees.util.RotorBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockModularBeehive extends BlockMBGuiBase<TileModularBeehive> implements ConnectBlock, Hive {

    public BlockModularBeehive() {
        super(hive());
    }

    @Override
    public TagKey<Block> harvestTool() {
        return BlockTags.MINEABLE_WITH_AXE;
    }

    @Override
    public void openGui(TileModularBeehive tile, Player p) {
        // Force structure check when opening GUI
        tile.formStructure();
        if (tile.isFormed()) {
            MBGuiHandler.open(ContainerMBModularBeehive.TYPE.type(), p, ContainerResolver.of(tile));
        } else {
            p.displayClientMessage(Component.translatable("modularbees.chat.beehive_unformed"), true);
        }
    }

    @Override
    public boolean canConnect(BlockGetter world, BlockPos otherPos) {
        return world.getBlockState(otherPos).getBlock() instanceof Hive;
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
    protected void loadBlockModel(DyResourcePack pack) {
        // NO-OP
    }

}
