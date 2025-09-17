package com.glodblock.github.modularbees.client;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.gui.MBAlvearyGui;
import com.glodblock.github.modularbees.client.gui.MBFeederGui;
import com.glodblock.github.modularbees.client.gui.MBModularBeehiveGui;
import com.glodblock.github.modularbees.client.gui.MBOverclockerGui;
import com.glodblock.github.modularbees.client.gui.MBTreaterGui;
import com.glodblock.github.modularbees.client.model.ModularConnectModelLoader;
import com.glodblock.github.modularbees.container.ContainerMBAlveary;
import com.glodblock.github.modularbees.container.ContainerMBFeeder;
import com.glodblock.github.modularbees.container.ContainerMBModularBeehive;
import com.glodblock.github.modularbees.container.ContainerMBOverclocker;
import com.glodblock.github.modularbees.container.ContainerMBTreater;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.util.FastColor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class MBClientRegistryHandler {

    public static final MBClientRegistryHandler INSTANCE = new MBClientRegistryHandler();

    @SubscribeEvent
    public void registerModels(ModelEvent.RegisterGeometryLoaders event) {
        event.register(ModularBees.id("connect_model"), ModularConnectModelLoader.LOADER);
    }

    @SubscribeEvent
    public void registerGui(RegisterMenuScreensEvent event) {
        event.register(ContainerMBModularBeehive.TYPE.castType(), MBModularBeehiveGui::new);
        event.register(ContainerMBAlveary.TYPE.castType(), MBAlvearyGui::new);
        event.register(ContainerMBFeeder.TYPE.castType(), MBFeederGui::new);
        event.register(ContainerMBOverclocker.TYPE.castType(), MBOverclockerGui::new);
        event.register(ContainerMBTreater.TYPE.castType(), MBTreaterGui::new);
    }

    private static ItemColor makeOpaque(ItemColor itemColor) {
        return (stack, tintIndex) -> FastColor.ARGB32.opaque(itemColor.getColor(stack, tintIndex));
    }

}
