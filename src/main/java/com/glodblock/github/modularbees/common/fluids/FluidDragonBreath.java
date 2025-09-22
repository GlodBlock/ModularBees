package com.glodblock.github.modularbees.common.fluids;

import com.glodblock.github.modularbees.common.MBSingletons;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public abstract class FluidDragonBreath extends BaseFlowingFluid {

    public static final FluidType TYPE = new FluidType(FluidType.Properties.create()
            .canExtinguish(true)
            .supportsBoating(true)
            .fallDistanceModifier(0)
            .temperature(1000)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BOTTLE_FILL_DRAGONBREATH)
            .lightLevel(8));

    protected FluidDragonBreath() {
        super(new Properties(() -> TYPE, FluidDragonBreath::getFluid, FluidDragonBreath::getFlowFluid)
                .slopeFindDistance(8)
                .explosionResistance(100)
                .bucket(() -> MBSingletons.DRAGON_BREATH_BUCKET)
                .block(() -> MBSingletons.DRAGON_BREATH)
        );
    }

    public static BaseFlowingFluid getFluid() {
        return Still.INSTANCE;
    }

    public static BaseFlowingFluid getFlowFluid() {
        return Flow.INSTANCE;
    }

    @Override
    protected ParticleOptions getDripParticle() {
        return ParticleTypes.DRAGON_BREATH;
    }

    @Override
    public @NotNull Fluid getFlowing() {
        return Flow.INSTANCE;
    }

    @Override
    public @NotNull Fluid getSource() {
        return Still.INSTANCE;
    }

    private static class Still extends FluidDragonBreath {

        static FluidDragonBreath INSTANCE = new Still();

        @Override
        public boolean isSource(@NotNull FluidState state) {
            return true;
        }

        @Override
        public int getAmount(@NotNull FluidState state) {
            return 8;
        }

    }

    private static class Flow extends FluidDragonBreath {

        static FluidDragonBreath INSTANCE = new Flow();

        @Override
        protected void createFluidStateDefinition(StateDefinition.@NotNull Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(@NotNull FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(@NotNull FluidState state) {
            return false;
        }

    }

}
