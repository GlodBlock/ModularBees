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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
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
    protected ResourceHandler<@NotNull ItemResource> exposed = new CombinedResourceHandler<>(this.outputs, this.inputs);
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
    public ResourceHandler<@NotNull ItemResource> getItemInventory() {
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
        if (this.isLoaded()) {
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
                    try (var trans = Transaction.openRoot()) {
                        var removed = this.energy.extract(power, trans);
                        if (removed >= power) {
                            this.process += this.tickSpeed;
                            trans.commit();
                        }
                    }
                } else {
                    this.process = 0;
                    this.runRecipe();
                }
            }
        }
    }

    @Override
    public void onChange(ResourceHandler<@NotNull ItemResource> inv, int slot) {
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
    public void saveTag(ValueOutput data) {
        super.saveTag(data);
        this.outputs.serialize(data.child("outputs"));
        this.inputs.serialize(data.child("inputs"));
        this.upgrade.serialize(data.child("upgrade"));
        data.putFloat("process", this.process);
    }

    @Override
    public void loadTag(ValueInput data) {
        super.loadTag(data);
        data.child("outputs").ifPresent(this.outputs::deserialize);
        data.child("inputs").ifPresent(this.inputs::deserialize);
        data.child("upgrade").ifPresent(this.upgrade::deserialize);
        this.process = data.getFloatOr("process", 0);
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
