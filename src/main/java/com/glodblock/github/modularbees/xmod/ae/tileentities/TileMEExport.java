package com.glodblock.github.modularbees.xmod.ae.tileentities;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEItemKey;
import com.glodblock.github.modularbees.common.MBConfig;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.util.ServerTickTile;
import com.glodblock.github.modularbees.xmod.ae.expose.MEExportAction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class TileMEExport extends TileAENetworkHost implements ServerTickTile, MEExportAction {

    private static final int ME_EXPORT_INTERVAL = MBConfig.ME_EXPORT_INTERVAL.get();
    private long tick = 0;
    protected final MBItemInventory config = new MBItemInventory(this, 12).setSlotLimit(1);
    protected boolean enableFilter = false;
    protected boolean whitelist = true;

    public TileMEExport(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * @param sending in-place modified
     */
    @Override
    public void sendToMENetwork(List<ItemStack> sending) {
        var storage = this.getMEStorage();
        if (storage != null) {
            for (var stack : sending) {
                if (!stack.isEmpty()) {
                    if (this.checkFilter(stack)) {
                        var inserted = storage.insert(AEItemKey.of(stack), stack.getCount(), Actionable.MODULATE, this.getSource());
                        stack.shrink((int) inserted);
                    }
                }
            }
        }
    }

    @Override
    public void tickServer(Level world, BlockState state) {
        if (this.isActive()) {
            this.tick++;
            if (this.tick % ME_EXPORT_INTERVAL == 0) {
                this.extractOutputs();
            }
        }
    }

    @Override
    public void saveTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.saveTag(data, provider);
        data.put("config", this.config.serializeNBT(provider));
        data.putBoolean("enableFilter", this.enableFilter);
        data.putBoolean("whitelist", this.whitelist);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.loadTag(data, provider);
        this.config.deserializeNBT(provider, data.getCompound("config"));
        this.enableFilter = data.getBoolean("enableFilter");
        this.whitelist = data.getBoolean("whitelist");
    }

    public MBItemInventory getConfig() {
        return this.config;
    }

    public boolean isEnableFilter() {
        return this.enableFilter;
    }

    public boolean isWhitelist() {
        return this.whitelist;
    }

    public void setEnableFilter(boolean enableFilter) {
        this.enableFilter = enableFilter;
    }

    public void setWhitelist(boolean whitelist) {
        this.whitelist = whitelist;
    }

    protected boolean checkFilter(ItemStack stack) {
        if (this.enableFilter) {
            if (this.whitelist) {
                return this.getConfig().contains(filter -> ItemStack.isSameItemSameComponents(filter, stack));
            } else {
                return !this.getConfig().contains(filter -> ItemStack.isSameItemSameComponents(filter, stack));
            }
        }
        return true;
    }

    protected abstract void extractOutputs();

    public abstract Component getDisplayName();

}
