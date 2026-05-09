package com.glodblock.github.modularbees.xmod.ae.tileentities;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.common.tileentities.centrifuge.TileModularCentrifuge;
import com.glodblock.github.modularbees.xmod.ae.AEXSingletons;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class TileMECentrifugeExport extends TileMEExport {

    public TileMECentrifugeExport(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileMECentrifugeExport.class, TileMECentrifugeExport::new, AEXSingletons.ME_CENTRIFUGE_EXPORT), pos, state);
    }

    @Override
    protected Direction getFacing() {
        return AEXSingletons.ME_CENTRIFUGE_EXPORT.getFacing(this.getBlockState());
    }

    @Override
    protected ItemLike getRepresentativeItem() {
        return AEXSingletons.ME_CENTRIFUGE_EXPORT;
    }

    @Override
    protected void extractOutputs() {
        if (this.core instanceof TileModularCentrifuge centrifuge) {
            var storage = this.getMEStorage();
            if (storage == null) {
                return;
            }
            var outputInv = centrifuge.getHandlerByName("outputs");
            if (outputInv != null) {
                for (var slot = 0; slot < outputInv.getSlots(); slot++) {
                    var stack = outputInv.getStackInSlot(slot);
                    if (!stack.isEmpty() && this.checkFilter(stack)) {
                        var inserted = storage.insert(AEItemKey.of(stack), stack.getCount(), Actionable.MODULATE, this.getSource());
                        outputInv.forceExtractItem(slot, (int) inserted, false);
                    }
                }
            }
            var fluidTank = centrifuge.getFluidInventory();
            if (fluidTank != null) {
                for (var slot = 0; slot < fluidTank.getTanks(); slot++) {
                    var tank = fluidTank.getTank(slot);
                    var fluid = tank.getFluid();
                    if (!fluid.isEmpty()) {
                        var inserted = storage.insert(AEFluidKey.of(fluid), tank.getFluidAmount(), Actionable.MODULATE, this.getSource());
                        tank.drain((int) inserted, IFluidHandler.FluidAction.EXECUTE);
                    }
                }
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return AEXSingletons.ME_CENTRIFUGE_EXPORT.getName();
    }

}
