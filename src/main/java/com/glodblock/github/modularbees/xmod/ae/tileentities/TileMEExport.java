package com.glodblock.github.modularbees.xmod.ae.tileentities;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import com.glodblock.github.modularbees.common.MBConfig;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.util.ServerTickTile;
import com.glodblock.github.modularbees.xmod.ae.expose.MEExportAction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;

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
    public void sendToMENetworkFluid(List<FluidStack> filling) {
        var storage = this.getMEStorage();
        if (storage != null) {
            for (var stack : filling) {
                if (!stack.isEmpty()) {
                    var inserted = storage.insert(AEFluidKey.of(stack), stack.getAmount(), Actionable.MODULATE, this.getSource());
                    stack.shrink((int) inserted);
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
    public void saveTag(ValueOutput data) {
        super.saveTag(data);
        this.config.serialize(data.child("config"));
        data.putBoolean("enable_filter", this.enableFilter);
        data.putBoolean("whitelist", this.whitelist);
    }

    @Override
    public void loadTag(ValueInput data) {
        super.loadTag(data);
        data.child("config").ifPresent(this.config::deserialize);
        this.enableFilter = data.getBooleanOr("enable_filter", false);
        this.whitelist = data.getBooleanOr("whitelist", false);
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
        if (this.enableFilter != enableFilter) {
            this.enableFilter = enableFilter;
            this.markDirty();
        }
    }

    public void setWhitelist(boolean whitelist) {
        if (this.whitelist != whitelist) {
            this.whitelist = whitelist;
            this.markDirty();
        }
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
