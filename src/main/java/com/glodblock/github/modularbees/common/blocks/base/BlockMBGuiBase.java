package com.glodblock.github.modularbees.common.blocks.base;

import com.glodblock.github.modularbees.common.tileentities.base.TileMBBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class BlockMBGuiBase<T extends TileMBBase> extends BlockMBTileBase<T> {

    public BlockMBGuiBase(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player p, @NotNull BlockHitResult hit) {
        var be = this.getBlockEntity(level, pos);
        if (be != null) {
            if (!level.isClientSide()) {
                this.openGui(be, p);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack heldItem, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player p, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        var parent = super.useItemOn(heldItem, state, level, pos, p, hand, hit);
        if (parent.result() != InteractionResult.PASS) {
            return parent;
        }
        if (p.isShiftKeyDown()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } else {
            var be = this.getBlockEntity(level, pos);
            if (be != null) {
                var ir = check(be, heldItem, level, pos, hit, p);
                if (ir != null) {
                    return ir;
                }
                if (!level.isClientSide()) {
                    this.openGui(be, p);
                }
                return ItemInteractionResult.SUCCESS;
            } else {
                return ItemInteractionResult.FAIL;
            }
        }
    }

    public abstract void openGui(T tile, Player p);

    @Nullable
    public ItemInteractionResult check(T tile, ItemStack stack, Level world, BlockPos pos, BlockHitResult hit, Player p) {
        return null;
    }

}
