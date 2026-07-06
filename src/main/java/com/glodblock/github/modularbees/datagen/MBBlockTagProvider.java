package com.glodblock.github.modularbees.datagen;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.common.MBRegistryHandler;
import com.glodblock.github.modularbees.common.blocks.base.BlockMBBase;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class MBBlockTagProvider extends BlockTagsProvider {

    public MBBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ModularBees.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        for (var block : MBRegistryHandler.INSTANCE.getBlocks()) {
            if (block instanceof BlockMBBase base) {
                var tool = base.harvestTool();
                if (tool != null) {
                    if (base.isOptionalBlock()) {
                        tag(tool).addOptional(base);
                    } else {
                        tag(tool).add(block);
                    }
                }
            }
        }
    }
}
