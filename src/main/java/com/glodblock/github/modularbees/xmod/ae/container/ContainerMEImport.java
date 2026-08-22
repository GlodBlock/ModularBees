package com.glodblock.github.modularbees.xmod.ae.container;

import com.glodblock.github.modularbees.container.base.ContainerMBBase;
import com.glodblock.github.modularbees.container.base.MBGuiHandler;
import com.glodblock.github.modularbees.container.slot.DisplaySlot;
import com.glodblock.github.modularbees.xmod.ae.tileentities.TileMEImport;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class ContainerMEImport extends ContainerMBBase<TileMEImport> {

    public final static MBGuiHandler.MenuHandler TYPE = MBGuiHandler.builder(TileMEImport.class)
            .factory(ContainerMEImport::new)
            .build("modular_me_import");

    protected ContainerMEImport(@Nullable MenuType<?> type, int id, Inventory inv, TileMEImport host) {
        super(type, id, inv, host);
        this.addConfigSlot(host.getConfig());
        this.addItemHandlerSlot(host.getStorage(), 98, 26, 3);
        this.bindPlayerInventorySlots(inv);
    }

    private void addConfigSlot(IItemHandler config) {
        for (int index = 0; index < config.getSlots(); index ++) {
            int x = index % 3;
            int y = index / 3;
            this.addSlot(new DisplaySlot(config, index, 26 + x * 18, 26 + y * 18));
        }
    }

    @Override
    protected int getHeight() {
        return 176;
    }

    @Override
    protected int getWidth() {
        return 176;
    }

}
