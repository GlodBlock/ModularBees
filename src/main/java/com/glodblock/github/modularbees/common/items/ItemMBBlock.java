package com.glodblock.github.modularbees.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemMBBlock extends BlockItem {

    private final Block blockType;

    public ItemMBBlock(Block block, Properties properties) {
        super(block, properties);
        this.blockType = block;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public final void appendHoverText(@NotNull ItemStack itemStack, Item.@NotNull TooltipContext context, @NotNull List<Component> toolTip, @NotNull TooltipFlag advancedTooltips) {
        this.addCheckedInformation(itemStack, context, toolTip, advancedTooltips);
    }

    @OnlyIn(Dist.CLIENT)
    public void addCheckedInformation(ItemStack itemStack, Item.TooltipContext context, List<Component> toolTip, TooltipFlag advancedTooltips) {
        this.blockType.appendHoverText(itemStack, context, toolTip, advancedTooltips);
    }

    @Override
    public boolean isBookEnchantable(@NotNull ItemStack stack, @NotNull ItemStack book) {
        return false;
    }

    @Override
    public @NotNull String getDescriptionId(@NotNull ItemStack is) {
        return this.blockType.getDescriptionId();
    }

}
