package com.glodblock.github.modularbees;

import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.client.MBClientRegistryHandler;
import com.glodblock.github.modularbees.common.MBConfig;
import com.glodblock.github.modularbees.common.MBRegistryHandler;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.fluids.FluidDragonBreath;
import com.glodblock.github.modularbees.common.hooks.TooltipHook;
import com.glodblock.github.modularbees.common.recipe.ElectrodeRecipe;
import com.glodblock.github.modularbees.dynamic.DyDataPack;
import com.glodblock.github.modularbees.dynamic.DyResourcePack;
import com.glodblock.github.modularbees.dynamic.MBPackSource;
import com.glodblock.github.modularbees.network.MBNetworkHandler;
import com.glodblock.github.modularbees.util.CombCentrifugeLookup;
import com.glodblock.github.modularbees.util.DataProvider;
import com.glodblock.github.modularbees.xmod.ModIDs;
import com.glodblock.github.modularbees.xmod.cc.PeripheralInit;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(ModularBees.MODID)
public class ModularBees {

    public static final String MODID = "modularbees";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ModularBees INSTANCE;
    public static final DyDataPack DATA_PACK = new DyDataPack();
    public static final DyResourcePack RESOURCE_PACK = new DyResourcePack();

    public ModularBees(IEventBus bus, ModContainer container) {
        assert INSTANCE == null;
        INSTANCE = this;
        if (!container.getModId().equals(MODID)) {
            throw new IllegalArgumentException("Invalid ID: " + MODID);
        }
        container.registerConfig(ModConfig.Type.COMMON, MBConfig.SPEC);
        bus.addListener((RegisterEvent e) -> {
            if (e.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
                MBRegistryHandler.INSTANCE.registerTab(e.getRegistry(Registries.CREATIVE_MODE_TAB));
                return;
            }
            if (e.getRegistryKey().equals(Registries.BLOCK)) {
                MBSingletons.init(MBRegistryHandler.INSTANCE);
                MBRegistryHandler.INSTANCE.runRegister();
            }
        });
        if (FMLEnvironment.dist.isClient()) {
            bus.register(MBClientRegistryHandler.INSTANCE);
        }
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::addPackFinders);
        bus.addListener(MBNetworkHandler.INSTANCE::onRegister);
        bus.register(MBRegistryHandler.INSTANCE);
        NeoForge.EVENT_BUS.register(TooltipHook.INSTANCE);
        NeoForge.EVENT_BUS.register(ElectrodeRecipe.class);
        NeoForge.EVENT_BUS.register(CombCentrifugeLookup.class);
    }

    public void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            RESOURCE_PACK.clear(event.getPackType());
            event.addRepositorySource(new MBPackSource("modularbees:dynamic_assets", event.getPackType(), RESOURCE_PACK::build));
        } else if (event.getPackType() == PackType.SERVER_DATA) {
            DATA_PACK.clear(event.getPackType());
            this.collectData();
            event.addRepositorySource(new MBPackSource("modularbees:dynamic_data", event.getPackType(), DATA_PACK::build));
        }
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        MBRegistryHandler.INSTANCE.init();
        if (GlodUtil.checkMod(ModIDs.CC)) {
            PeripheralInit.init();
        }
    }

    public void clientSetup(FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(FluidDragonBreath.getFluid(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(FluidDragonBreath.getFlowFluid(), RenderType.translucent());
    }

    public static ResourceLocation id(String id) {
        return ResourceLocation.fromNamespaceAndPath(MODID, id);
    }

    private void collectData() {
        for (var item : MBRegistryHandler.INSTANCE.getItems()) {
            if (item instanceof DataProvider provider) {
                provider.load(DATA_PACK);
            }
        }
    }

}
