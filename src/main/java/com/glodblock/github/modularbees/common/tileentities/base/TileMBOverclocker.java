package com.glodblock.github.modularbees.common.tileentities.base;

import com.glodblock.github.modularbees.common.MBConfig;
import com.glodblock.github.modularbees.common.caps.EnergyHandlerHost;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.inventory.MBEnergyInventory;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.common.inventory.SlotListener;
import com.glodblock.github.modularbees.common.recipe.ElectrodeRecipe;
import com.glodblock.github.modularbees.util.GameConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class TileMBOverclocker extends TileMBModularComponent implements ItemHandlerHost, EnergyHandlerHost, SlotListener {

    public static final int POWER_USE = MBConfig.OVERCLOCKER_POWER_USAGE.get();
    protected final MBItemInventory electrode = new MBItemInventory(this, 1).setSlotLimit(1).setFilter(this::isElectrode).inputOnly();
    protected final MBEnergyInventory energy = new MBEnergyInventory(this, 2 * GameConstants.M).inputOnly();
    private ElectrodeRecipe running = null;
    private boolean stuck = false;

    public TileMBOverclocker(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public float getBoostAndConsume(int bees) {
        if (this.level instanceof ServerLevel server && this.isActive() && bees > 0) {
            var stack = this.electrode.getStackInSlot(0);
            if (!stack.isEmpty()) {
                if (this.running == null) {
                    this.running = this.findRecipe(stack);
                }
                if (this.running != null) {
                    var boost = this.running.power() - 1;
                    if (boost > 0 && this.energy.getEnergyStored() >= bees * POWER_USE) {
                        int power = this.energy.forceExtractEnergy(bees * POWER_USE, false);
                        if (power >= bees * POWER_USE) {
                            stack.hurtAndBreak(this.damage(bees), server, null, item -> this.electrode.setStackInSlot(0, ItemStack.EMPTY));
                            return boost;
                        }
                    }
                }
            }
        }
        return 0;
    }

    private int damage(int base) {
        float a = base / 15F + 1;
        float e = (float) Math.pow(1.03, base);
        return (int) (a * e);
    }

    @Override
    public void saveTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.saveTag(data, provider);
        data.put("electrode", this.electrode.serializeNBT(provider));
        data.put("energy", this.energy.serializeNBT(provider));
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.loadTag(data, provider);
        this.electrode.deserializeNBT(provider, data.getCompound("electrode"));
        this.energy.deserializeNBT(provider, data.getCompound("energy"));
    }

    @Override
    public IItemHandler getItemInventory() {
        return this.electrode;
    }

    @Override
    public MBEnergyInventory getEnergyStorage() {
        return this.energy;
    }

    @Override
    public MBEnergyInventory getEnergyStorage(Direction side) {
        if (side == null) {
            return this.getEnergyStorage();
        }
        if (side == this.getFacing()) {
            return this.energy;
        }
        return null;
    }

    @Override
    public MBItemInventory getHandlerByName(String name) {
        return this.electrode;
    }

    @Override
    public void addInventoryDrops(Level level, @NotNull BlockPos pos, List<ItemStack> drops) {
        drops.addAll(this.electrode.toList());
    }

    public abstract Direction getFacing();

    public abstract Component getDisplayName();

    @Nullable
    private ElectrodeRecipe findRecipe(ItemStack stack) {
        if (this.stuck || this.level == null) {
            return null;
        }
        return ElectrodeRecipe.getCache(this.level).get(stack.getItem());
    }

    private boolean isElectrode(ItemStack stack) {
        if (this.level == null) {
            return false;
        }
        return ElectrodeRecipe.getCache(this.level).containsKey(stack.getItem());
    }

    @Override
    public void onChange(IItemHandler inv, int slot) {
        if (inv == this.electrode) {
            this.stuck = false;
            this.running = null;
        }
    }

}
