package com.glodblock.github.modularbees.datagen;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.common.fluids.FluidDragonBreath;
import com.glodblock.github.modularbees.util.MBTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class MBFluidTagProvider extends FluidTagsProvider {

    public MBFluidTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ModularBees.MODID);
    }

    @Override
    public void addTags(HolderLookup.@NotNull Provider provider) {
        tag(MBTags.DRAGON_BREATH)
                .add(FluidDragonBreath.getFluid())
                .add(FluidDragonBreath.getFlowFluid());
    }

}
