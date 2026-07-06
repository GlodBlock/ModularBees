package com.glodblock.github.modularbees.common.blocks.centrifuge;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.util.ConnectBlock;
import com.glodblock.github.modularbees.common.blocks.base.BlockMBGuiBase;
import com.glodblock.github.modularbees.common.tileentities.centrifuge.TileCentrifugeGearbox;
import com.glodblock.github.modularbees.container.ContainerMBGearbox;
import com.glodblock.github.modularbees.container.base.MBGuiHandler;
import com.glodblock.github.modularbees.dynamic.DyResourcePack;
import com.glodblock.github.modularbees.util.ContainerResolver;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

public class BlockCentrifugeGearbox extends BlockMBGuiBase<TileCentrifugeGearbox> implements ConnectBlock, Centrifuge {

    public BlockCentrifugeGearbox(BlockBehaviour.Properties properties) {
        super(centrifuge(properties));
    }

    @Override
    public TagKey<@NotNull Block> harvestTool() {
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
    public void openGui(TileCentrifugeGearbox tile, Player p) {
        MBGuiHandler.open(ContainerMBGearbox.TYPE.type(), p, ContainerResolver.of(tile));
    }

    @Override
    public Identifier modelType() {
        return ModularBees.id("modular_connect_model");
    }

}