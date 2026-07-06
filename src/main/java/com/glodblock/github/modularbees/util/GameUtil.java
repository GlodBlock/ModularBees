package com.glodblock.github.modularbees.util;

import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveAlveary;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.init.ModItems;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Containers;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GameUtil {

    public static final DecimalFormat NUMBER_F = new DecimalFormat("#,###.##");
    public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, TileBeehiveAlveary.@NotNull AlvearyBee> BEE_CODEC = StreamCodec.composite(
            BeehiveBlockEntity.Occupant.STREAM_CODEC, TileBeehiveAlveary.AlvearyBee::toOccupant,
            ByteBufCodecs.BOOL, TileBeehiveAlveary.AlvearyBee::getLink,
            TileBeehiveAlveary.AlvearyBee::new
    );
    public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull List<TileBeehiveAlveary.AlvearyBee>> BEES_CODEC = StreamCodec.of(
            (buf, list) -> {
                buf.writeInt(list.size());
                list.forEach(bee -> BEE_CODEC.encode(buf, bee));
            }, buf -> {
                List<TileBeehiveAlveary.AlvearyBee> list = new ArrayList<>();
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
                                    Item.CODEC_WITH_BOUND_COMPONENTS.fieldOf("id").forGetter(ItemStack::typeHolder),
                                    Codec.INT.fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
                                    DataComponentPatch.CODEC
                                            .optionalFieldOf("components", DataComponentPatch.EMPTY)
                                            .forGetter(ItemStack::getComponentsPatch)
                            )
                            .apply(stack, ItemStack::new)
            )
    );
    public static final Codec<byte[]> BYTE_ARR_CODEC = Codec.BYTE_BUFFER.xmap(
            ByteBuffer::array,
            ByteBuffer::wrap
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

    public static FluidStack getFluidFromItem(ItemStack stack, ItemAccess access) {
        var tank = stack.getCapability(Capabilities.Fluid.ITEM, access);
        if (tank != null) {
            for (int x = 0; x < tank.size(); x ++) {
                if (!tank.getResource(x).isEmpty()) {
                    return tank.getResource(x).toStack(tank.getAmountAsInt(x));
                }
            }
        }
        return FluidStack.EMPTY;
    }

    public static void saveItemList(List<ItemStack> items, ValueOutput output, String name) {
        if (!items.isEmpty()) {
            var vList = output.childrenList(name);
            for (var stack : items) {
                if (!stack.isEmpty()) {
                    var tag = vList.addChild();
                    tag.store("s", GameUtil.UNLIMITED_ITEM_CODEC, stack);
                }
            }
        }
    }

    public static void loadItemList(List<ItemStack> items, ValueInput input, String name) {
        input.childrenList(name).ifPresent(vList -> {
            for (var stack : vList) {
                stack.read("s", GameUtil.UNLIMITED_ITEM_CODEC).ifPresent(items::add);
            }
        });
    }

    public static void saveFluidList(List<FluidStack> fluids, ValueOutput output, String name) {
        if (!fluids.isEmpty()) {
            var vList = output.childrenList(name);
            for (var stack : fluids) {
                if (!stack.isEmpty()) {
                    var tag = vList.addChild();
                    tag.store("s", FluidStack.CODEC, stack);
                }
            }
        }
    }

    public static void loadFluidList(List<FluidStack> fluids, ValueInput input, String name) {
        input.childrenList(name).ifPresent(vList -> {
            for (var stack : vList) {
                stack.read("s", FluidStack.CODEC).ifPresent(fluids::add);
            }
        });
    }

    public static String readableCount(int count) {
        if (count < 1000) {
            return Integer.toString(count);
        }
        if (count < 10_000) {
            float k = count / 1000f;
            return String.format("%.1f", k) + "k";
        }
        int k = count / 1000;
        return k + "k";
    }

    public static ItemStack emptyCage(ItemStack cage) {
        if (cage.getItem() == ModItems.STURDY_BEE_CAGE.get()) {
            return new ItemStack(ModItems.STURDY_BEE_CAGE);
        } else {
            return ItemStack.EMPTY;
        }
    }

    public static boolean fastDamage(ItemStack stack, int damage) {
        if (stack.isDamageableItem()) {
            var dmg = stack.getDamageValue() + damage;
            if (dmg >= stack.getMaxDamage()) {
                return true;
            } else {
                stack.setDamageValue(dmg);
            }
        }
        return false;
    }

    @SafeVarargs
    public static <T> T coalesce(T... values) {
        for (var value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public static <T extends Resource> void transferResource(ResourceHandler<@NotNull T> from, ResourceHandler<@NotNull T> to) {
        for (int x = 0; x < from.size(); x ++) {
            var resource = from.getResource(x);
            var stored = from.getAmountAsInt(x);
            if (resource.isEmpty() || stored <= 0) {
                continue;
            }
            try (var trans = Transaction.openRoot()) {
                var added = to.insert(resource, stored, trans);
                if (added > 0) {
                    var rmv = from.extract(x, resource, added, trans);
                    if (rmv == added) {
                        trans.commit();
                    }
                }
            }
        }
    }

}
