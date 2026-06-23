package com.glodblock.github.modularbees.mixins;

import com.glodblock.github.modularbees.xmod.mek.CardboxWrap;
import mekanism.common.item.block.ItemBlockCardboardBox;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBlockCardboardBox.class)
public abstract class ItemBlockCardboardBoxMixin {

    @Inject(
            method = "onItemUseFirst",
            at = @At(value = "FIELD", target = "Lmekanism/common/CommonWorldTickHandler;monitoringCardboardBox:Z", ordinal = 0),
            remap = false
    )
    private void wrap(ItemStack stack, UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        var world = context.getLevel();
        var pos = context.getClickedPos();
        var te = world.getBlockEntity(pos);
        if (te instanceof CardboxWrap wrap) {
            wrap.onWrap();
        }
    }

}
