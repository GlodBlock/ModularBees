package com.glodblock.github.modularbees.xmod.ae.tileentities;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import com.glodblock.github.modularbees.common.tileentities.centrifuge.TileModularCentrifuge;
import com.glodblock.github.modularbees.xmod.ae.AEXSingletons;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class TileMECentrifugeExport extends TileMEExport {

    public TileMECentrifugeExport(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected Direction getFacing() {
        return AEXSingletons.ME_CENTRIFUGE_EXPORT.get().getFacing(this.getBlockState());
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
                for (var slot = 0; slot < outputInv.size(); slot++) {
                    var stack = outputInv.getItemStack(slot);
                    if (!stack.isEmpty() && this.checkFilter(stack)) {
                        try (var trans = Transaction.openRoot()) {
                            var inserted = storage.insert(AEItemKey.of(stack), stack.getCount(), Actionable.MODULATE, this.getSource());
                            if (inserted > 0) {
                                outputInv.forceExtract(slot, ItemResource.of(stack), (int) inserted, trans);
                                trans.commit();
                            }
                        }
                    }
                }
            }
            var tank = centrifuge.getFluidInventory();
            if (tank != null) {
                for (var slot = 0; slot < tank.size(); slot++) {
                    var fluid = tank.getFluidStack(slot);
                    if (!fluid.isEmpty()) {
                        try (var trans = Transaction.openRoot()) {
                            var inserted = storage.insert(AEFluidKey.of(fluid), fluid.getAmount(), Actionable.MODULATE, this.getSource());
                            if (inserted > 0) {
                                tank.forceExtract(slot, FluidResource.of(fluid), (int) inserted, trans);
                                trans.commit();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return AEXSingletons.ME_CENTRIFUGE_EXPORT.get().getName();
    }

}
