package com.glodblock.github.modularbees.xmod.ae;

import com.glodblock.github.modularbees.xmod.ae.container.ContainerMEExport;
import com.glodblock.github.modularbees.xmod.ae.container.ContainerMEImport;
import com.glodblock.github.modularbees.xmod.ae.gui.MEExportGUI;
import com.glodblock.github.modularbees.xmod.ae.gui.MEImportGUI;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class AEXClientLoader {

    public static void initGui(RegisterMenuScreensEvent event) {
        event.register(ContainerMEExport.TYPE.castType(), MEExportGUI::new);
        event.register(ContainerMEImport.TYPE.castType(), MEImportGUI::new);
    }

}
