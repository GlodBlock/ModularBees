package com.glodblock.github.modularbees.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;

public class MBCodec {

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

}
