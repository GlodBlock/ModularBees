package com.glodblock.github.modularbees.common.tileentities.centrifuge;

import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.caps.EnergyHandlerHost;
import com.glodblock.github.modularbees.common.inventory.MBEnergyInventory;
import com.glodblock.github.modularbees.util.GameConstants;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class TileCentrifugeHeater extends TileCentrifugePart implements EnergyHandlerHost {

    public static final int POWER_USE = ProductiveBeesConfig.GENERAL.centrifugePowerUse.get();
    protected final MBEnergyInventory energy = new MBEnergyInventory(this, 2 * GameConstants.M).inputOnly();

    public TileCentrifugeHeater(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public boolean check(int para) {
        if (this.isActive() && para > 0) {
            if (this.energy.getAmountAsInt() >= para * POWER_USE) {
                try (var trans = Transaction.openRoot()) {
                    int power = this.energy.forceExtract(para * POWER_USE, trans);
                    if (power >= para * POWER_USE) {
                        trans.commit();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void saveTag(ValueOutput data) {
        super.saveTag(data);
        this.energy.serialize(data.child("energy"));
    }

    @Override
    public void loadTag(ValueInput data) {
        super.loadTag(data);
        data.child("energy").ifPresent(this.energy::deserialize);
    }

    @Override
    public MBEnergyInventory getEnergyStorage() {
        return this.energy;
    }

    public Direction getFacing() {
        return MBSingletons.MODULAR_CENTRIFUGE_HEATER.get().getFacing(this.getBlockState());
    }

}
