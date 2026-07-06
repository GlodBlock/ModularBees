package com.glodblock.github.modularbees.mixins;

import com.glodblock.github.modularbees.common.inventory.MBBigItemInventory;
import com.glodblock.github.modularbees.container.slot.MBInventorySlot;
import com.glodblock.github.modularbees.util.GameUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
    public int imageWidth;

    @Inject(
            method = "renderSlotContents",
            at = @At("HEAD"),
            cancellable = true
    )
    private void renderBigInventorySlot(GuiGraphicsExtractor graphics, ItemStack itemStack, Slot slot, String itemCount, CallbackInfo ci) {
        if (slot instanceof MBInventorySlot mb) {
            var inv = mb.getResourceHandler();
            if (inv instanceof MBBigItemInventory) {
                var stack = itemStack.copyWithCount(1);
                graphics.item(stack, slot.x, slot.y, slot.x + slot.y * this.imageWidth);
                graphics.itemDecorations(this.font, stack, slot.x, slot.y, GameUtil.readableCount(itemStack.getCount()));
                ci.cancel();
            }
        }
    }

    protected AbstractContainerScreenMixin(Component p_96550_) {
        super(p_96550_);
    }

}
