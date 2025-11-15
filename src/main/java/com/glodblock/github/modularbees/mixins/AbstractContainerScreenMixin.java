package com.glodblock.github.modularbees.mixins;

import com.glodblock.github.modularbees.common.inventory.MBBigItemInventory;
import com.glodblock.github.modularbees.container.slot.MBInventorySlot;
import com.glodblock.github.modularbees.util.GameUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {

    @Shadow
    protected int imageWidth;

    @Inject(
            method = "renderSlotContents",
            at = @At("HEAD"),
            cancellable = true
    )
    private void renderBigInventorySlot(GuiGraphics guiGraphics, ItemStack itemstack, Slot slot, String countString, CallbackInfo ci) {
        if (slot instanceof MBInventorySlot mb) {
            var inv = mb.getItemHandler();
            if (inv instanceof MBBigItemInventory) {
                var stack = itemstack.copyWithCount(1);
                guiGraphics.renderItem(stack, slot.x, slot.y, slot.x + slot.y * this.imageWidth);
                guiGraphics.renderItemDecorations(this.font, stack, slot.x, slot.y, GameUtil.readableCount(itemstack.getCount()));
                ci.cancel();
            }
        }
    }

    protected AbstractContainerScreenMixin(Component p_96550_) {
        super(p_96550_);
    }

}
