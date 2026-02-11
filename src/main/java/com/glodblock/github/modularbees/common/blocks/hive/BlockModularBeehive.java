package com.glodblock.github.modularbees.common.blocks.hive;

import com.glodblock.github.modularbees.client.util.ConnectBlock;
import com.glodblock.github.modularbees.common.blocks.base.BlockMBGuiBase;
import com.glodblock.github.modularbees.common.tileentities.hive.TileModularBeehive;
import com.glodblock.github.modularbees.container.ContainerMBModularBeehive;
import com.glodblock.github.modularbees.container.base.MBGuiHandler;
import com.glodblock.github.modularbees.dynamic.DyResourcePack;
import com.glodblock.github.modularbees.network.MBNetworkHandler;
import com.glodblock.github.modularbees.network.SMBHighlighter;
import com.glodblock.github.modularbees.util.ContainerResolver;
import com.glodblock.github.modularbees.util.GameConstants;
import com.glodblock.github.modularbees.util.GameUtil;
import com.glodblock.github.modularbees.util.RotorBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class BlockModularBeehive extends BlockMBGuiBase<TileModularBeehive> implements ConnectBlock, Hive {

    public BlockModularBeehive() {
        super(hive());
    }

    @Override
    public TagKey<Block> harvestTool() {
        return BlockTags.MINEABLE_WITH_AXE;
    }

    @Override
    public ItemInteractionResult check(TileModularBeehive tile, ItemStack stack, Level world, BlockPos pos, BlockHitResult hit, Player p) {
        if (!world.isClientSide()) {
            var tank = tile.getFluidInventory();
            if (stack.is(Items.GLASS_BOTTLE) && tank.getFluidAmount() >= GameConstants.BOTTLE) {
                stack.shrink(1);
                tank.drain(GameConstants.BOTTLE, IFluidHandler.FluidAction.EXECUTE);
                if (!p.addItem(new ItemStack(Items.HONEY_BOTTLE))) {
                    GameUtil.spawnDrops(world, p.getOnPos(), List.of(new ItemStack(Items.HONEY_BOTTLE)));
                }
                return ItemInteractionResult.SUCCESS;
            } else if (stack.is(Items.BUCKET) && tank.getFluidAmount() >= GameConstants.BUCKET) {
                stack.shrink(1);
                tank.drain(GameConstants.BUCKET, IFluidHandler.FluidAction.EXECUTE);
                if (!p.addItem(new ItemStack(ModItems.HONEY_BUCKET))) {
                    GameUtil.spawnDrops(world, p.getOnPos(), List.of(new ItemStack(ModItems.HONEY_BUCKET)));
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return null;
    }

    @Override
    public void openGui(TileModularBeehive tile, Player p) {
        // Force structure check when opening GUI
        tile.formStructure();
        if (tile.isFormed()) {
            MBGuiHandler.open(ContainerMBModularBeehive.TYPE.type(), p, ContainerResolver.of(tile));
        } else {
            p.displayClientMessage(Objects.requireNonNullElse(tile.getUnformedMessage(), Component.translatable("modularbees.chat.beehive_unformed")), true);
            if (tile.getErrorInfo() instanceof BlockPos pos && p instanceof ServerPlayer sp) {
                MBNetworkHandler.INSTANCE.sendTo(new SMBHighlighter(pos, p.level().dimension()), sp);
            }
        }
    }

    @Override
    public boolean canConnect(BlockGetter world, BlockPos otherPos) {
        return world.getBlockState(otherPos).getBlock() instanceof Hive;
    }

    @Override
    protected void onFacingChange(Direction facing, Level world, BlockPos pos) {
        var tile = this.getBlockEntity(world, pos);
        if (tile != null) {
            tile.invalidateCapabilities();
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

    @Override
    protected void loadBlockModel(DyResourcePack pack) {
        // NO-OP
    }

}
