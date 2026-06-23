package com.glodblock.github.modularbees.common.blocks.misc;

import com.glodblock.github.modularbees.common.fluids.FluidDragonBreath;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

public class BlockFluidDragonBreath extends LiquidBlock {

    public BlockFluidDragonBreath() {
        super(FluidDragonBreath.getFluid(), Properties.of().noCollission().strength(100.0F).noLootTable().mapColor(MapColor.TERRACOTTA_PURPLE).replaceable().liquid().noLootTable());
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos position, @NotNull Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.HARM, 10, 0, false, false));
        }
        super.entityInside(state, world, position, entity);
    }

}
