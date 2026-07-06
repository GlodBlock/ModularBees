package com.glodblock.github.modularbees.mixins;

import com.glodblock.github.modularbees.util.ResourceHandlerAccessor;
import net.neoforged.neoforge.transfer.StacksResourceHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StacksResourceHandler.class)
public abstract class StacksResourceHandlerMixin implements ResourceHandlerAccessor {

}
