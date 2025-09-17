package com.glodblock.github.modularbees.mixins;

import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BeehiveBlockEntity.BeeData.class)
public abstract class BeeDataMixin {

    @Shadow
    @Final
    private BeehiveBlockEntity.Occupant occupant;

    @Override
    public int hashCode() {
        return this.occupant.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BeehiveBlockEntity.BeeData other) {
            return other.toOccupant().equals(this.occupant);
        }
        return false;
    }

}
