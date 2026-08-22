package com.glodblock.github.modularbees.xmod.ae.tileentities;

import com.glodblock.github.modularbees.common.MBConfig;
import com.glodblock.github.modularbees.common.inventory.MBBigItemInventory;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.util.ServerTickTile;
import com.glodblock.github.modularbees.xmod.ae.expose.MEImportAction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class TileMEImport extends TileAENetworkHost implements ServerTickTile, MEImportAction {

    private static final int ME_IMPORT_INTERVAL = MBConfig.ME_IMPORT_INTERVAL.get();
    private static final int BUFFER_MULTIPLIER = 8;
    private long tick = 0;
    protected final MBItemInventory config = new MBItemInventory(this, 9).setFilter(this::isValid).setSlotLimit(1);
    protected final MBItemInventory storage = new MBBigItemInventory(this, BUFFER_MULTIPLIER, 9);

    public TileMEImport(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public MBItemInventory getConfig() {
        return this.config;
    }

    public MBItemInventory getStorage() {
        return this.storage;
    }

    protected final int getDelta(int slot) {
        var stored = this.storage.getStackInSlot(slot);
        var config = this.config.getStackInSlot(slot);
        if (stored.isEmpty() && config.isEmpty()) {
            return 0;
        }
        if (stored.isEmpty()) {
            return config.getMaxStackSize() * BUFFER_MULTIPLIER;
        }
        if (config.isEmpty()) {
            return -stored.getCount();
        }
        if (ItemStack.isSameItemSameComponents(stored, config)) {
            return stored.getMaxStackSize() * BUFFER_MULTIPLIER - stored.getCount();
        } else {
            return -stored.getCount();
        }
    }

    @Override
    public void addInventoryDrops(Level level, @NotNull BlockPos pos, List<ItemStack> drops) {
        drops.addAll(this.storage.toList());
    }

    @Override
    public void tickServer(Level world, BlockState state) {
        if (this.isActive()) {
            this.tick++;
            if (this.tick % ME_IMPORT_INTERVAL == 0) {
                this.updateStorage();
            }
        }
    }

    @Override
    public void saveTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.saveTag(data, provider);
        data.put("config", this.config.serializeNBT(provider));
        data.put("storage", this.storage.serializeNBT(provider));
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.loadTag(data, provider);
        this.config.deserializeNBT(provider, data.getCompound("config"));
        this.storage.deserializeNBT(provider, data.getCompound("storage"));
    }

    @Override
    public IItemHandlerModifiable getItemInventory() {
        return this.storage;
    }

    public abstract Component getDisplayName();

    protected abstract void updateStorage();

    protected abstract boolean isValid(ItemStack stack);

}
