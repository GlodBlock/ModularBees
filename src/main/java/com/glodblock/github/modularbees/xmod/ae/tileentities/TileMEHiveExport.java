package com.glodblock.github.modularbees.xmod.ae.tileentities;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import com.glodblock.github.modularbees.common.tileentities.hive.TileModularBeehive;
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

public class TileMEHiveExport extends TileMEExport {

    public TileMEHiveExport(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected Direction getFacing() {
        return AEXSingletons.ME_BEEHIVE_EXPORT.get().getFacing(this.getBlockState());
    }

    @Override
    protected ItemLike getRepresentativeItem() {
        return AEXSingletons.ME_BEEHIVE_EXPORT;
    }

    @Override
    protected void extractOutputs() {
        if (this.core instanceof TileModularBeehive hive) {
            var storage = this.getMEStorage();
            if (storage == null) {
                return;
            }
            var outputInv = hive.getHandlerByName("outputs");
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
            var fluidTank = hive.getFluidInventory();
            if (fluidTank != null) {
                var honey = fluidTank.getFluidStack(0);
                if (!honey.isEmpty()) {
                    var inserted = storage.insert(AEFluidKey.of(honey), honey.getAmount(), Actionable.MODULATE, this.getSource());
                    try (var trans = Transaction.openRoot()) {
                        fluidTank.forceExtract(0, FluidResource.of(honey), (int) inserted, trans);
                        trans.commit();
                    }
                }
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return AEXSingletons.ME_BEEHIVE_EXPORT.get().getName();
    }

}
