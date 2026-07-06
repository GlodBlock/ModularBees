package com.glodblock.github.modularbees;

import com.glodblock.github.modularbees.client.MBClientRegistryHandler;
import com.glodblock.github.modularbees.client.util.SyncRecipes;
import com.glodblock.github.modularbees.common.MBConfig;
import com.glodblock.github.modularbees.common.MBRegistryHandler;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.hooks.TooltipHook;
import com.glodblock.github.modularbees.common.recipe.ElectrodeRecipe;
import com.glodblock.github.modularbees.dynamic.DyDataPack;
import com.glodblock.github.modularbees.dynamic.DyResourcePack;
import com.glodblock.github.modularbees.dynamic.MBPackSource;
import com.glodblock.github.modularbees.network.MBNetworkHandler;
import com.glodblock.github.modularbees.util.CombCentrifugeLookup;
import com.glodblock.github.modularbees.util.DataProvider;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
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
    public static IEventBus MOD_BUS;

    public ModularBees(IEventBus bus, ModContainer container) {
        assert INSTANCE == null;
        INSTANCE = this;
        if (!container.getModId().equals(MODID)) {
            throw new IllegalArgumentException("Invalid ID: " + MODID);
        }
        ModularBees.MOD_BUS = bus;
        MBRegistryHandler.INSTANCE = new MBRegistryHandler(bus);
        MBSingletons.init(MBRegistryHandler.INSTANCE);
        container.registerConfig(ModConfig.Type.COMMON, MBConfig.SPEC);
        bus.addListener((RegisterEvent e) -> {
            if (e.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
                MBRegistryHandler.INSTANCE.registerTab(e.getRegistry(Registries.CREATIVE_MODE_TAB));
            }
        });
        if (FMLEnvironment.getDist().isClient()) {
            bus.register(MBClientRegistryHandler.INSTANCE);
        }
        bus.addListener(this::addPackFinders);
        bus.addListener(MBNetworkHandler.INSTANCE::onRegister);
        NeoForge.EVENT_BUS.register(TooltipHook.INSTANCE);
        NeoForge.EVENT_BUS.register(SyncRecipes.INSTANCE);
        NeoForge.EVENT_BUS.register(ElectrodeRecipe.class);
        NeoForge.EVENT_BUS.register(CombCentrifugeLookup.class);
        InterModComms.sendTo("invtweaks", "blacklist-screen", () -> "com.glodblock.github.modularbees.client.gui.*");
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

    public static Identifier id(String id) {
        return Identifier.fromNamespaceAndPath(MODID, id);
    }

    public static String stringId(String id) {
        return id(id).toString();
    }

    private void collectData() {
        for (var item : MBRegistryHandler.INSTANCE.getItems()) {
            if (item instanceof DataProvider provider) {
                provider.load(DATA_PACK);
            }
        }
    }

}
