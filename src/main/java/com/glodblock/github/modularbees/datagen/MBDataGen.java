package com.glodblock.github.modularbees.datagen;

import com.glodblock.github.modularbees.ModularBees;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.util.Util;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

@EventBusSubscriber(modid = ModularBees.MODID)
public class MBDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client dataEvent) {
        var pack = dataEvent.getGenerator().getVanillaPack(true);
        var lookup = dataEvent.getLookupProvider();
        CompletableFuture<HolderLookup.Provider> registries = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
        pack.addProvider(c -> new MBBlockTagProvider(c, lookup));
        pack.addProvider(c -> new MBFluidTagProvider(c, lookup));
        pack.addProvider(c -> new MBItemTagProvider(c, lookup));
        pack.addProvider(bindRegistries(MBRecipeProvider.Runner::new, registries));
    }

    private static <T extends DataProvider> DataProvider.Factory<@NotNull T> bindRegistries(BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, T> target, CompletableFuture<HolderLookup.Provider> registries) {
        return output -> target.apply(output, registries);
    }

}
