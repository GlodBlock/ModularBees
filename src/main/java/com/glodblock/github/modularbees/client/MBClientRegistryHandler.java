package com.glodblock.github.modularbees.client;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.gui.MBAlvearyGui;
import com.glodblock.github.modularbees.client.gui.MBBeeExtractorGui;
import com.glodblock.github.modularbees.client.gui.MBDragonGui;
import com.glodblock.github.modularbees.client.gui.MBFeederGui;
import com.glodblock.github.modularbees.client.gui.MBGearboxGui;
import com.glodblock.github.modularbees.client.gui.MBHeaterGui;
import com.glodblock.github.modularbees.client.gui.MBImportGui;
import com.glodblock.github.modularbees.client.gui.MBModularBeehiveGui;
import com.glodblock.github.modularbees.client.gui.MBModularCentrifugeGui;
import com.glodblock.github.modularbees.client.gui.MBOverclockerGui;
import com.glodblock.github.modularbees.client.gui.MBTreaterGui;
import com.glodblock.github.modularbees.client.model.ConnectWrapItemModel;
import com.glodblock.github.modularbees.client.model.ModularConnectModel;
import com.glodblock.github.modularbees.client.model.ModularConnectModelLoader;
import com.glodblock.github.modularbees.common.fluids.FluidDragonBreath;
import com.glodblock.github.modularbees.container.ContainerMBAlveary;
import com.glodblock.github.modularbees.container.ContainerMBBeeExtractor;
import com.glodblock.github.modularbees.container.ContainerMBDragon;
import com.glodblock.github.modularbees.container.ContainerMBFeeder;
import com.glodblock.github.modularbees.container.ContainerMBGearbox;
import com.glodblock.github.modularbees.container.ContainerMBHeater;
import com.glodblock.github.modularbees.container.ContainerMBImport;
import com.glodblock.github.modularbees.container.ContainerMBModularBeehive;
import com.glodblock.github.modularbees.container.ContainerMBModularCentrifuge;
import com.glodblock.github.modularbees.container.ContainerMBOverclocker;
import com.glodblock.github.modularbees.container.ContainerMBTreater;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.event.RegisterItemModelsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;

public class MBClientRegistryHandler {

    public static final MBClientRegistryHandler INSTANCE = new MBClientRegistryHandler();

    @SubscribeEvent
    public void registerClientExt(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {

            @Override
            public Identifier getRenderOverlayTexture(@NotNull Minecraft mc) {
                return ModularBees.id("textures/block/dragon_breath_under.png");
            }

        }, FluidDragonBreath.TYPE);
    }

    @SubscribeEvent
    public void registerModels(RegisterFluidModelsEvent event) {
        event.register(new FluidModel.Unbaked(
                new Material(ModularBees.id("block/dragon_breath_still")),
                new Material(ModularBees.id("block/dragon_breath_flow")),
                new Material(ModularBees.id("block/dragon_breath_flow")),
                null, null
        ), FluidDragonBreath::getFluid, FluidDragonBreath::getFlowFluid);
    }

    @SubscribeEvent
    public void registerModels(ModelEvent.RegisterLoaders event) {
        event.register(ModularBees.id("connect_model"), ModularConnectModelLoader.LOADER);
    }

    @SubscribeEvent
    public void registerModels(RegisterBlockStateModels event) {
        event.registerModel(ModularBees.id("modular_connect_model"), ModularConnectModel.CODEC);
    }

    @SubscribeEvent
    public void registerModels(RegisterItemModelsEvent event) {
        event.register(ModularBees.id("modular_connect_model"), ConnectWrapItemModel.CODEC);
    }

    @SubscribeEvent
    public void registerGui(RegisterMenuScreensEvent event) {
        event.register(ContainerMBModularBeehive.TYPE.castType(), MBModularBeehiveGui::new);
        event.register(ContainerMBAlveary.TYPE.castType(), MBAlvearyGui::new);
        event.register(ContainerMBFeeder.TYPE.castType(), MBFeederGui::new);
        event.register(ContainerMBOverclocker.TYPE.castType(), MBOverclockerGui::new);
        event.register(ContainerMBTreater.TYPE.castType(), MBTreaterGui::new);
        event.register(ContainerMBDragon.TYPE.castType(), MBDragonGui::new);
        event.register(ContainerMBModularCentrifuge.TYPE.castType(), MBModularCentrifugeGui::new);
        event.register(ContainerMBImport.TYPE.castType(), MBImportGui::new);
        event.register(ContainerMBHeater.TYPE.castType(), MBHeaterGui::new);
        event.register(ContainerMBGearbox.TYPE.castType(), MBGearboxGui::new);
        event.register(ContainerMBBeeExtractor.TYPE.castType(), MBBeeExtractorGui::new);
    }

}
