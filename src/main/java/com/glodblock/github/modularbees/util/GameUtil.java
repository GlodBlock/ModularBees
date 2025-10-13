package com.glodblock.github.modularbees.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Containers;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GameUtil {

    public static final DecimalFormat NUMBER_F = new DecimalFormat("#,###.##");
    public static final StreamCodec<RegistryFriendlyByteBuf, BeehiveBlockEntity.BeeData> BEE_CODEC = StreamCodec.composite(
            BeehiveBlockEntity.Occupant.STREAM_CODEC, BeehiveBlockEntity.BeeData::toOccupant, BeehiveBlockEntity.BeeData::new
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, List<BeehiveBlockEntity.BeeData>> BEES_CODEC = StreamCodec.of(
            (buf, list) -> {
                buf.writeInt(list.size());
                list.forEach(bee -> BEE_CODEC.encode(buf, bee));
            }, buf -> {
                List<BeehiveBlockEntity.BeeData> list = new ArrayList<>();
                int size = buf.readInt();
                for (int i = 0; i < size; i ++) {
                    list.add(BEE_CODEC.decode(buf));
                }
                return list;
            }
    );
    public static final Codec<ItemStack> UNLIMITED_ITEM_CODEC = Codec.lazyInitialized(
            () -> RecordCodecBuilder.create(
                    stack -> stack.group(
                                    ItemStack.ITEM_NON_AIR_CODEC.fieldOf("id").forGetter(ItemStack::getItemHolder),
                                    Codec.INT.fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
                                    DataComponentPatch.CODEC
                                            .optionalFieldOf("components", DataComponentPatch.EMPTY)
                                            .forGetter(ItemStack::getComponentsPatch)
                            )
                            .apply(stack, ItemStack::new)
            )
    );
    public static final Hash.Strategy<ItemStack> ITEM_HASH = new Hash.Strategy<>() {
        @Override
        public int hashCode(@Nullable ItemStack stack) {
            if (stack == null) {
                return 0;
            }
            return ItemStack.hashItemAndComponents(stack);
        }

        @Override
        public boolean equals(@Nullable ItemStack s1, @Nullable ItemStack s2) {
            if (s1 == s2) {
                return true;
            }
            if (s1 == null || s2 == null) {
                return false;
            }
            return ItemStack.isSameItemSameComponents(s1, s2);
        }
    };

    public static void spawnDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        if (!level.isClientSide()) {
            for (var i : drops) {
                if (!i.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), i);
                }
            }
        }
    }

    public static BlockState getBlockFromItem(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem bi) {
            return bi.getBlock().defaultBlockState();
        }
        return Blocks.AIR.defaultBlockState();
    }

    public static FluidStack getFluidFromItem(ItemStack stack) {
        var tank = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (tank != null) {
            for (int x = 0; x < tank.getTanks(); x ++) {
                if (!tank.getFluidInTank(x).isEmpty()) {
                    return tank.getFluidInTank(x);
                }
            }
        }
        return FluidStack.EMPTY;
    }

}
