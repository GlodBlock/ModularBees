package com.glodblock.github.modularbees.xmod.ae.tileentities;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.common.tileentities.hive.TileModularBeehive;
import com.glodblock.github.modularbees.xmod.ae.AEXSingletons;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class TileMEHiveExport extends TileMEExport {

    public TileMEHiveExport(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileMEHiveExport.class, TileMEHiveExport::new, AEXSingletons.ME_BEEHIVE_EXPORT), pos, state);
    }

    @Override
    protected Direction getFacing() {
        return AEXSingletons.ME_BEEHIVE_EXPORT.getFacing(this.getBlockState());
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
                for (var slot = 0; slot < outputInv.getSlots(); slot++) {
                    var stack = outputInv.getStackInSlot(slot);
                    if (!stack.isEmpty() && this.checkFilter(stack)) {
                        var inserted = storage.insert(AEItemKey.of(stack), stack.getCount(), Actionable.MODULATE, this.getSource());
                        outputInv.forceExtractItem(slot, (int) inserted, false);
                    }
                }
            }
            var fluidTank = hive.getFluidInventory();
            if (fluidTank != null) {
                var honey = fluidTank.getFluid();
                if (!honey.isEmpty()) {
                    var inserted = storage.insert(AEFluidKey.of(honey), honey.getAmount(), Actionable.MODULATE, this.getSource());
                    fluidTank.forceDrain((int) inserted, IFluidHandler.FluidAction.EXECUTE);
                }
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return AEXSingletons.ME_BEEHIVE_EXPORT.getName();
    }

}
