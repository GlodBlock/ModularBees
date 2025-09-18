package com.glodblock.github.modularbees.common.blocks.misc;

import com.glodblock.github.modularbees.common.blocks.base.BlockMBBase;
import com.glodblock.github.modularbees.dynamic.DyDataPack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockScentedPlank extends BlockMBBase {

    public BlockScentedPlank() {
        super(hive());
    }

    @Override
    public TagKey<Block> harvestTool() {
        return BlockTags.MINEABLE_WITH_AXE;
    }

    protected void loadBlockTag(DyDataPack pack) {
        super.loadBlockTag(pack);
        pack.addBlockTag(BlockTags.PLANKS, this.getRegistryName());
        pack.addItemTag(ItemTags.PLANKS, this.getRegistryName());
    }

    @Override
    public int getFlammability(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull Direction direction) {
        return 15;
    }

}
