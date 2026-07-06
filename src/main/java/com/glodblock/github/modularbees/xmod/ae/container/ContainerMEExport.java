package com.glodblock.github.modularbees.xmod.ae.container;

import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.container.base.ContainerMBBase;
import com.glodblock.github.modularbees.container.base.MBGuiHandler;
import com.glodblock.github.modularbees.container.slot.DisplaySlot;
import com.glodblock.github.modularbees.xmod.ae.tileentities.TileMEExport;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class ContainerMEExport extends ContainerMBBase<TileMEExport> {

    public final static MBGuiHandler.MenuHandler TYPE = MBGuiHandler.builder(TileMEExport.class)
            .factory(ContainerMEExport::new)
            .build("modular_me_export");

    protected ContainerMEExport(@Nullable MenuType<?> type, int id, Inventory inv, TileMEExport host) {
        super(type, id, inv, host);
        this.addConfigSlot(host.getConfig());
        this.bindPlayerInventorySlots(inv);
        this.getSync()
                .addBool(1, this::isEnableFilter, this::setEnableFilter)
                .addBool(2, this::isWhitelist, this::setWhitelist);
        this.getActionMap().put("enable_filter", o -> this.setEnableFilter(o.getBoolean()));
        this.getActionMap().put("filter_mode", o -> this.setWhitelist(o.getBoolean()));
    }

    private void addConfigSlot(MBItemInventory config) {
        for (int index = 0; index < config.size(); index ++) {
            int x = index % 4;
            int y = index / 4;
            this.addSlot(new DisplaySlot(config, index, 53 + x * 18, 17 + y * 18));
        }
    }

    public boolean isEnableFilter() {
        return this.getHost().isEnableFilter();
    }

    public boolean isWhitelist() {
        return this.getHost().isWhitelist();
    }

    public void setEnableFilter(boolean enableFilter) {
        this.getHost().setEnableFilter(enableFilter);
    }

    public void setWhitelist(boolean whitelist) {
        this.getHost().setWhitelist(whitelist);
    }

    @Override
    protected int getHeight() {
        return 166;
    }

    @Override
    protected int getWidth() {
        return 176;
    }

}
