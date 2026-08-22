package com.glodblock.github.modularbees.xmod.ae;

import com.glodblock.github.modularbees.xmod.ae.container.ContainerMEExport;
import com.glodblock.github.modularbees.xmod.ae.container.ContainerMEImport;

public class AEXCommonLoader {

    public static void init() {
        ContainerMEExport.TYPE.register();
        ContainerMEImport.TYPE.register();
    }

}
