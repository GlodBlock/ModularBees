package com.glodblock.github.modularbees.datagen;

import com.glodblock.github.modularbees.ModularBees;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class MBItemTagProvider extends ItemTagsProvider {

    public MBItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ModularBees.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

    }

}