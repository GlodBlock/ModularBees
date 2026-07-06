package com.glodblock.github.modularbees.common.blocks.hive;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.util.ConnectBlock;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.blocks.base.BlockMBGuiBase;
import com.glodblock.github.modularbees.common.fluids.FluidDragonBreath;
import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveDragon;
import com.glodblock.github.modularbees.container.ContainerMBDragon;
import com.glodblock.github.modularbees.container.base.MBGuiHandler;
import com.glodblock.github.modularbees.dynamic.DyResourcePack;
import com.glodblock.github.modularbees.util.ContainerResolver;
import com.glodblock.github.modularbees.util.GameConstants;
import com.glodblock.github.modularbees.util.GameUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockBeehiveDragon extends BlockMBGuiBase<TileBeehiveDragon> implements ConnectBlock, Hive {

    public BlockBeehiveDragon(BlockBehaviour.Properties properties) {
        super(hive(properties));
    }

    @Override
    public InteractionResult check(TileBeehiveDragon tile, ItemStack stack, Level world, BlockPos pos, BlockHitResult hit, Player p) {
        if (!world.isClientSide()) {
            var tank = tile.getFluidInventory();
            if (stack.is(Items.GLASS_BOTTLE) && tank.getAmountAsInt(0) >= GameConstants.BOTTLE) {
                try (var trans = Transaction.openRoot()) {
                    var removed = tank.extract(0, FluidResource.of(FluidDragonBreath.getFluid()), GameConstants.BOTTLE, trans);
                    if (removed == GameConstants.BOTTLE) {
                        stack.shrink(1);
                        if (!p.addItem(new ItemStack(Items.DRAGON_BREATH))) {
                            GameUtil.spawnDrops(world, p.getOnPos(), List.of(new ItemStack(Items.DRAGON_BREATH)));
                        }
                        trans.commit();
                    }
                }
                return InteractionResult.SUCCESS;
            } else if (stack.is(Items.BUCKET) && tank.getAmountAsInt(0) >= GameConstants.BUCKET) {
                try (var trans = Transaction.openRoot()) {
                    var removed = tank.extract(0, FluidResource.of(FluidDragonBreath.getFluid()), GameConstants.BUCKET, trans);
                    if (removed == GameConstants.BUCKET) {
                        stack.shrink(1);
                        if (!p.addItem(MBSingletons.DRAGON_BREATH_BUCKET.toStack())) {
                            GameUtil.spawnDrops(world, p.getOnPos(), List.of(MBSingletons.DRAGON_BREATH_BUCKET.toStack()));
                        }
                        trans.commit();
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return null;
    }

    @Override
    public void openGui(TileBeehiveDragon tile, Player p) {
        MBGuiHandler.open(ContainerMBDragon.TYPE.type(), p, ContainerResolver.of(tile));
    }

    @Override
    public TagKey<@NotNull Block> harvestTool() {
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

    @Override
    public Identifier modelType() {
        return ModularBees.id("modular_connect_model");
    }

}
