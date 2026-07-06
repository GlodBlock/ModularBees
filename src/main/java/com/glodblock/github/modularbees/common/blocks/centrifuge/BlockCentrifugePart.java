package com.glodblock.github.modularbees.common.blocks.centrifuge;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.util.ConnectBlock;
import com.glodblock.github.modularbees.common.blocks.base.BlockMBTileBase;
import com.glodblock.github.modularbees.common.tileentities.centrifuge.TileCentrifugePart;
import com.glodblock.github.modularbees.dynamic.DyResourcePack;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

public class BlockCentrifugePart extends BlockMBTileBase<TileCentrifugePart> implements ConnectBlock, Centrifuge {

    public BlockCentrifugePart(BlockBehaviour.Properties properties) {
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
    public Identifier modelType() {
        return ModularBees.id("modular_connect_model");
    }

}
