package com.glodblock.github.modularbees.common.tileentities.centrifuge;

import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.caps.EnergyHandlerHost;
import com.glodblock.github.modularbees.common.inventory.MBEnergyInventory;
import com.glodblock.github.modularbees.util.GameConstants;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileCentrifugeHeater extends TileCentrifugePart implements EnergyHandlerHost {

    public static final int POWER_USE = ProductiveBeesConfig.GENERAL.centrifugePowerUse.get();
    protected final MBEnergyInventory energy = new MBEnergyInventory(this, 2 * GameConstants.M).inputOnly();

    public TileCentrifugeHeater(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileCentrifugeHeater.class, TileCentrifugeHeater::new, MBSingletons.MODULAR_CENTRIFUGE_HEATER), pos, state);
    }

    public boolean check(int para) {
        if (this.isActive() && para > 0) {
            if (this.energy.getEnergyStored() >= para * POWER_USE) {
                int power = this.energy.forceExtractEnergy(para * POWER_USE, false);
                return power >= para * POWER_USE;
            }
        }
        return false;
    }

    @Override
    public void saveTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.saveTag(data, provider);
        data.put("energy", this.energy.serializeNBT(provider));
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.loadTag(data, provider);
        this.energy.deserializeNBT(provider, data.getCompound("energy"));
    }

    @Override
    public MBEnergyInventory getEnergyStorage() {
        return this.energy;
    }

    public Direction getFacing() {
        return MBSingletons.MODULAR_CENTRIFUGE_HEATER.getFacing(this.getBlockState());
    }

}
