package com.glodblock.github.modularbees.datagen;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.common.MBRegistryHandler;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.blocks.base.BlockMBBase;
import com.glodblock.github.modularbees.util.MBTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class MBBlockTagProvider extends BlockTagsProvider {

    public MBBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ModularBees.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        for (var block : MBRegistryHandler.INSTANCE.getBlocks()) {
            if (block instanceof BlockMBBase base) {
                var tool = base.harvestTool();
                if (tool != null) {
                    tag(tool).add(block);
                }
            }
        }
        tag(MBTags.SOUL_BLACKLIST).add(MBSingletons.MODULAR_BEEHIVE_CORE);
        tag(MBTags.JDT_BLACKLIST).add(MBSingletons.MODULAR_BEEHIVE_CORE);
        tag(MBTags.TIAB_BLACKLIST).add(MBSingletons.MODULAR_BEEHIVE_CORE);
    }
}
