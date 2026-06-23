package com.glodblock.github.modularbees.common.blocks.hive;

import com.glodblock.github.modularbees.client.util.ConnectBlock;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.blocks.base.BlockMBGuiBase;
import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveDragon;
import com.glodblock.github.modularbees.container.ContainerMBDragon;
import com.glodblock.github.modularbees.container.base.MBGuiHandler;
import com.glodblock.github.modularbees.dynamic.DyResourcePack;
import com.glodblock.github.modularbees.util.ContainerResolver;
import com.glodblock.github.modularbees.util.GameConstants;
import com.glodblock.github.modularbees.util.GameUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

public class BlockBeehiveDragon extends BlockMBGuiBase<TileBeehiveDragon> implements ConnectBlock, Hive {

    public BlockBeehiveDragon() {
        super(hive());
    }

    @Override
    public ItemInteractionResult check(TileBeehiveDragon tile, ItemStack stack, Level world, BlockPos pos, BlockHitResult hit, Player p) {
        if (!world.isClientSide()) {
            var tank = tile.getFluidInventory();
            if (stack.is(Items.GLASS_BOTTLE) && tank.getFluidAmount() >= GameConstants.BOTTLE) {
                stack.shrink(1);
                tank.drain(GameConstants.BOTTLE, IFluidHandler.FluidAction.EXECUTE);
                if (!p.addItem(new ItemStack(Items.DRAGON_BREATH))) {
                    GameUtil.spawnDrops(world, p.getOnPos(), List.of(new ItemStack(Items.DRAGON_BREATH)));
                }
                return ItemInteractionResult.SUCCESS;
            } else if (stack.is(Items.BUCKET) && tank.getFluidAmount() >= GameConstants.BUCKET) {
                stack.shrink(1);
                tank.drain(GameConstants.BUCKET, IFluidHandler.FluidAction.EXECUTE);
                if (!p.addItem(new ItemStack(MBSingletons.DRAGON_BREATH_BUCKET))) {
                    GameUtil.spawnDrops(world, p.getOnPos(), List.of(new ItemStack(MBSingletons.DRAGON_BREATH_BUCKET)));
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return null;
    }

    @Override
    public void openGui(TileBeehiveDragon tile, Player p) {
        MBGuiHandler.open(ContainerMBDragon.TYPE.type(), p, ContainerResolver.of(tile));
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
