package com.glodblock.github.modularbees.xmod.ae;

import com.glodblock.github.glodium.registry.RegistryHandler;
import com.glodblock.github.glodium.xmod.ThirdParty;
import com.glodblock.github.glodium.xmod.XModLoader;
import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.common.MBRegistryHandler;
import com.glodblock.github.modularbees.xmod.ModIDs;
import com.glodblock.github.modularbees.xmod.ae.container.ContainerMEExport;
import com.glodblock.github.modularbees.xmod.ae.gui.MEExportGUI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@ThirdParty(ModIDs.AE2)
public class AE2Loader implements XModLoader {

    @Override
    public String modid() {
        return ModIDs.AE2;
    }

    @Override
    public void loadCommon() {

    }

    @Override
    public void loadClient() {

    }

    @Override
    public void onRegister(RegistryHandler handler) {
        AEXSingletons.init((MBRegistryHandler) handler);
        if (FMLEnvironment.getDist().isClient()) {
            ModularBees.MOD_BUS.addListener(this::bindGui);
        }
        ModularBees.MOD_BUS.addListener(this::registerContainer);
    }

    public void bindGui(RegisterMenuScreensEvent event) {
        event.register(ContainerMEExport.TYPE.castType(), MEExportGUI::new);
    }

    public void registerContainer(RegisterEvent e) {
        if (e.getRegistry().equals(BuiltInRegistries.MENU)) {
            ContainerMEExport.TYPE.register();
        }
    }

}
