package com.glodblock.github.modularbees.common.tileentities.base;

import com.glodblock.github.modularbees.common.MBConfig;
import com.glodblock.github.modularbees.common.caps.EnergyHandlerHost;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.inventory.MBEnergyInventory;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.common.inventory.SlotListener;
import com.glodblock.github.modularbees.common.recipe.ElectrodeRecipe;
import com.glodblock.github.modularbees.util.GameConstants;
import com.glodblock.github.modularbees.util.GameUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
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
        if (this.isActive() && bees > 0) {
            var stack = this.electrode.getResource(0);
            if (!stack.isEmpty()) {
                if (this.running == null) {
                    this.running = this.findRecipe(stack.getItem());
                }
                if (this.running != null) {
                    var boost = this.running.power() - 1;
                    if (boost > 0 && this.energy.getAmountAsInt() >= bees * POWER_USE) {
                        try (var trans = Transaction.openRoot()) {
                            int power = this.energy.forceExtract(bees * POWER_USE, trans);
                            if (power >= bees * POWER_USE) {
                                var stored = this.electrode.getItemStack(0);
                                if (GameUtil.fastDamage(stored, this.damage(bees))) {
                                    this.electrode.setItemStack(0, ItemStack.EMPTY);
                                }
                                trans.commit();
                                return boost;
                            }
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
    public void saveTag(ValueOutput data) {
        super.saveTag(data);
        this.electrode.serialize(data.child("electrode"));
        this.energy.serialize(data.child("energy"));
    }

    @Override
    public void loadTag(ValueInput data) {
        super.loadTag(data);
        data.child("electrode").ifPresent(this.electrode::deserialize);
        data.child("energy").ifPresent(this.energy::deserialize);
    }

    @Override
    public MBItemInventory getItemInventory() {
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
    private ElectrodeRecipe findRecipe(Item stack) {
        if (this.stuck || !(this.level instanceof ServerLevel serverLevel)) {
            return null;
        }
        return ElectrodeRecipe.getCache(serverLevel).get(stack);
    }

    private boolean isElectrode(ItemResource stack) {
        if (!(this.level instanceof ServerLevel serverLevel)) {
            return false;
        }
        return ElectrodeRecipe.getCache(serverLevel).containsKey(stack.getItem());
    }

    @Override
    public void onChange(ResourceHandler<@NotNull ItemResource> inv, int slot) {
        if (inv == this.electrode) {
            this.stuck = false;
            this.running = null;
        }
    }

}
