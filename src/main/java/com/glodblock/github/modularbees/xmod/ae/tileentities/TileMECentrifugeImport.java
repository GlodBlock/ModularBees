package com.glodblock.github.modularbees.xmod.ae.tileentities;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEItemKey;
import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.common.tileentities.centrifuge.TileModularCentrifuge;
import com.glodblock.github.modularbees.util.CombCentrifugeLookup;
import com.glodblock.github.modularbees.xmod.ae.AEXSingletons;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;

public class TileMECentrifugeImport extends TileMEImport {

    public TileMECentrifugeImport(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileMECentrifugeImport.class, TileMECentrifugeImport::new, AEXSingletons.ME_CENTRIFUGE_IMPORT), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return AEXSingletons.ME_CENTRIFUGE_IMPORT.getName();
    }

    @Override
    protected void updateStorage() {
        if (this.core instanceof TileModularCentrifuge centrifuge) {
            var storage = this.getMEStorage();
            if (storage == null) {
                return;
            }
            boolean needUpdate = false;
            for (int slot = 0; slot < this.storage.getSlots(); slot++) {
                var delta = this.getDelta(slot);
                if (delta > 0) {
                    var stack = this.config.getStackInSlot(slot);
                    var added = storage.extract(AEItemKey.of(stack), delta, Actionable.MODULATE, this.getSource());
                    if (added > 0) {
                        this.storage.forceInsertItem(slot, stack.copyWithCount((int) added), false);
                        needUpdate = true;
                    }
                } else if (delta < 0) {
                    var stack = this.storage.getStackInSlot(slot);
                    var removed = storage.insert(AEItemKey.of(stack), -delta, Actionable.MODULATE, this.getSource());
                    stack.shrink((int) removed);
                    needUpdate = true;
                }
            }
            if (needUpdate) {
                centrifuge.unblock();
            }
        }
    }

    @Override
    protected boolean isValid(ItemStack stack) {
        return CombCentrifugeLookup.validInput(stack, this.level);
    }

    @Override
    protected Direction getFacing() {
        return AEXSingletons.ME_CENTRIFUGE_IMPORT.getFacing(this.getBlockState());
    }

    @Override
    protected ItemLike getRepresentativeItem() {
        return AEXSingletons.ME_CENTRIFUGE_IMPORT;
    }

}
