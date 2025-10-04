package com.glodblock.github.modularbees.common.hooks;

import com.glodblock.github.modularbees.common.recipe.ElectrodeRecipe;
import com.glodblock.github.modularbees.util.GameUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.IdentityHashMap;

public class TooltipHook {

    public static final TooltipHook INSTANCE = new TooltipHook();
    private static final IdentityHashMap<Item, Float> CACHE = new IdentityHashMap<>();

    private TooltipHook() {
        assert INSTANCE == null;
    }

    @SubscribeEvent
    public void onTooltipEvent(ItemTooltipEvent event) {
        var item = event.getItemStack().getItem();
        if (CACHE.containsKey(item)) {
            event.getToolTip().add(Component.translatable("modularbees.tooltip.electrode_power", GameUtil.NUMBER_F.format(CACHE.get(item))).withStyle(ChatFormatting.GOLD));
        }
    }

    @SubscribeEvent
    public void onRecipeUpdate(RecipesUpdatedEvent event) {
        CACHE.clear();
        var recipes = event.getRecipeManager().byType(ElectrodeRecipe.TYPE);
        for (var recipe : recipes) {
            CACHE.put(recipe.value().electrode().getItem(), recipe.value().power());
        }
    }

}
