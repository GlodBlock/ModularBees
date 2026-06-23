package com.glodblock.github.modularbees.datagen;

import com.glodblock.github.modularbees.ModularBees;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MBItemTagProvider extends ItemTagsProvider {

    public MBItemTagProvider(PackOutput p, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> block, @Nullable ExistingFileHelper existingFileHelper) {
        super(p, lookupProvider, block, ModularBees.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

    }
}