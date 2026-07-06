package com.glodblock.github.modularbees.common.blocks.hive;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.util.ConnectBlock;
import com.glodblock.github.modularbees.common.blocks.base.BlockMBGuiBase;
import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveAlveary;
import com.glodblock.github.modularbees.container.ContainerMBAlveary;
import com.glodblock.github.modularbees.container.base.MBGuiHandler;
import com.glodblock.github.modularbees.dynamic.DyResourcePack;
import com.glodblock.github.modularbees.util.ContainerResolver;
import com.glodblock.github.modularbees.util.GameUtil;
import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBee;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockBeehiveAlveary extends BlockMBGuiBase<TileBeehiveAlveary> implements ConnectBlock, Hive {

    public BlockBeehiveAlveary(BlockBehaviour.Properties properties) {
        super(hive(properties));
    }

    @Override
    public InteractionResult check(TileBeehiveAlveary tile, ItemStack stack, Level world, BlockPos pos, BlockHitResult hit, Player p) {
        if (!world.isClientSide()) {
            var one = stack.copyWithCount(1);
            if (BeeCage.isFilled(one) && tile.hasRoom()) {
                var bee = BeeCage.getEntityFromStack(one, world, true);
                if (bee == null) {
                    p.sendOverlayMessage(Component.translatable("modularbees.gui.modular_beehive_alveary.invalid"));
                    return InteractionResult.FAIL;
                }
                if (bee instanceof SolitaryBee) {
                    p.sendOverlayMessage(Component.translatable("modularbees.gui.modular_beehive_alveary.solitary"));
                    return InteractionResult.FAIL;
                }
                if (bee.getAge() < 0) {
                    p.sendOverlayMessage(Component.translatable("modularbees.gui.modular_beehive_alveary.child"));
                    return InteractionResult.FAIL;
                }
                tile.addBee(world, bee);
                tile.notifyCore();
                var empty = GameUtil.emptyCage(one);
                stack.shrink(1);
                if (!p.addItem(empty)) {
                    GameUtil.spawnDrops(world, p.getOnPos(), List.of(empty));
                }
                return InteractionResult.SUCCESS;
            } else if (tile.isEmptyCage(ItemResource.of(one)) && !tile.getBees().isEmpty()) {
                var entity = tile.getBees().getLast().toOccupant().createEntity(world, pos);
                if (entity instanceof Bee bee) {
                    bee.setHivePos(pos);
                    BeeCage.captureEntity(bee, one);
                    tile.getBees().removeLast();
                    tile.setChanged();
                    tile.notifyCore();
                    stack.shrink(1);
                    if (!p.addItem(one)) {
                        GameUtil.spawnDrops(world, p.getOnPos(), List.of(one));
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return null;
    }

    @Override
    public void openGui(TileBeehiveAlveary tile, Player p) {
        if (tile.isActive()) {
            tile.relink();
        }
        MBGuiHandler.open(ContainerMBAlveary.TYPE.type(), p, ContainerResolver.of(tile));
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
