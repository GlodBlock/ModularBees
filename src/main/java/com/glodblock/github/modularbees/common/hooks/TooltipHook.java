package com.glodblock.github.modularbees.common.hooks;

import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveOverclocker;
import com.glodblock.github.modularbees.util.GameUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class TooltipHook {

    public static final TooltipHook INSTANCE = new TooltipHook();

    private TooltipHook() {
        assert INSTANCE == null;
    }

    @SubscribeEvent
    public void onTooltipEvent(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof TileBeehiveOverclocker.HiveElectrode electrode) {
            event.getToolTip().add(Component.translatable("modularbees.tooltip.electrode_power", GameUtil.NUMBER_F.format(electrode.getPower())).withStyle(ChatFormatting.GOLD));
        }
    }

}
