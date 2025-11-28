package com.glodblock.github.modularbees.common.tileentities.base;

import com.glodblock.github.modularbees.common.caps.EnergyHandlerHost;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.inventory.MBEnergyInventory;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.common.inventory.SlotListener;
import com.glodblock.github.modularbees.util.GameConstants;
import com.glodblock.github.modularbees.util.ServerTickTile;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivelib.registry.LibItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public abstract class TileMBMachine extends TileMBBase implements ItemHandlerHost, EnergyHandlerHost, ServerTickTile, SlotListener {

    public static final int IDLE = 0;
    public static final int RUNNING = 1;
    public static final int STOP = 2;
    public static final Set<Item> TIME_UPGRADES = Set.of(LibItems.UPGRADE_TIME.get(), LibItems.UPGRADE_TIME_2.get());
    protected final MBItemInventory upgrade = new MBItemInventory(this, 4, s -> this.validUpgrades().contains(s.getItem())).setSlotLimit(1);
    protected final MBItemInventory inputs = this.createInputs();
    protected final MBItemInventory outputs = this.createOutputs();
    protected final MBEnergyInventory energy = new MBEnergyInventory(this, 80 * GameConstants.K);
    protected IItemHandlerModifiable exposed = new CombinedInvWrapper(this.outputs, this.inputs);
    protected float process = 0;
    protected float tickSpeed = 1;
    protected int powerMultiplier = 1;
    protected int statues = IDLE;
    protected boolean needCheck = true;

    public TileMBMachine(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void setProcess(double process) {
        this.process = (float) process;
    }

    public double getProcess() {
        return this.process;
    }

    @Override
    public IItemHandler getItemInventory() {
        return this.exposed;
    }

    @Override
    public MBEnergyInventory getEnergyStorage() {
        return this.energy;
    }

    protected abstract MBItemInventory createInputs();

    protected abstract MBItemInventory createOutputs();

    protected abstract Set<Item> validUpgrades();

    protected abstract int getMaxProcessTime();

    protected abstract int getPowerUse();

    @Override
    public MBItemInventory getHandlerByName(String name) {
        return switch (name) {
            case "upgrade" -> this.upgrade;
            case "inputs" -> this.inputs;
            case "outputs" -> this.outputs;
            default -> null;
        };
    }

    @Override
    public void tickServer(Level world, BlockState state) {
        if (!this.notLoaded()) {
            if (this.needCheck) {
                this.needCheck = false;
                this.statues = this.checkStatus();
            }
            if (this.statues == STOP) {
                this.process = 0;
                this.statues = IDLE;
            } else if (this.statues == RUNNING) {
                if (this.process < this.getMaxProcessTime()) {
                    var power = this.getPowerUse() * this.powerMultiplier;
                    if (this.energy.getEnergyStored() >= power) {
                        this.process += this.tickSpeed;
                        this.energy.forceExtractEnergy(power, false);
                    }
                } else {
                    this.process = 0;
                    this.runRecipe();
                }
            }
        }
    }

    @Override
    public void onChange(IItemHandler inv, int slot) {
        if (inv == this.upgrade) {
            this.updateUpgrade();
        } else if (inv == this.inputs || inv == this.outputs) {
            this.needCheck = true;
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.updateUpgrade();
    }

    @Override
    public void addInventoryDrops(Level level, @NotNull BlockPos pos, List<ItemStack> drops) {
        drops.addAll(this.outputs.toList());
        drops.addAll(this.inputs.toList());
        drops.addAll(this.upgrade.toList());
    }

    @Override
    public void saveTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.saveTag(data, provider);
        data.put("outputs", this.outputs.serializeNBT(provider));
        data.put("inputs", this.inputs.serializeNBT(provider));
        data.put("upgrade", this.upgrade.serializeNBT(provider));
        data.putFloat("process", this.process);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.loadTag(data, provider);
        this.outputs.deserializeNBT(provider, data.getCompound("outputs"));
        this.inputs.deserializeNBT(provider, data.getCompound("inputs"));
        this.upgrade.deserializeNBT(provider, data.getCompound("upgrade"));
        this.process = data.getFloat("process");
    }

    // You should only return RUNNING and STOP here.
    protected abstract int checkStatus();

    protected abstract void runRecipe();

    protected void updateUpgrade() {
        float timeDiscount = (float) ((this.upgrade.countStack(LibItems.UPGRADE_TIME.get()) + 2 * this.upgrade.countStack(LibItems.UPGRADE_TIME_2.get())) * ProductiveBeesConfig.UPGRADES.timeBonus.get());
        if (timeDiscount >= 1) {
            this.tickSpeed = this.getMaxProcessTime() * 2;
        } else {
            this.tickSpeed = 1 / (1 - timeDiscount);
        }
        this.powerMultiplier = (int) Math.max(timeDiscount, 1);
    }

}
