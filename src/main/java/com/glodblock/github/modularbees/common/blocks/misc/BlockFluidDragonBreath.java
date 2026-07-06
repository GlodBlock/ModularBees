package com.glodblock.github.modularbees.common.blocks.misc;

import com.glodblock.github.modularbees.common.fluids.FluidDragonBreath;
import com.glodblock.github.modularbees.util.Hidden;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

public class BlockFluidDragonBreath extends LiquidBlock implements Hidden {

    public BlockFluidDragonBreath(Properties properties) {
        super(FluidDragonBreath.getFluid(), properties.noCollision().strength(100.0F).noLootTable().mapColor(MapColor.TERRACOTTA_PURPLE).replaceable().liquid());
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos position, @NotNull Entity entity, @NotNull InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 10, 0, false, false));
        }
        super.entityInside(state, world, position, entity, effectApplier, isPrecise);
    }

}
