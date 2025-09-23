package com.glodblock.github.modularbees.datagen;

import com.glodblock.github.modularbees.ModularBees;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = ModularBees.MODID, bus = EventBusSubscriber.Bus.MOD)
public class MBDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent dataEvent) {
        var pack = dataEvent.getGenerator().getVanillaPack(true);
        var file = dataEvent.getExistingFileHelper();
        var lookup = dataEvent.getLookupProvider();
        var block = pack.addProvider(c -> new MBBlockTagProvider(c, lookup, file));
        pack.addProvider(c -> new MBFluidTagProvider(c, lookup, file));
        pack.addProvider(p -> new MBRecipeProvider(p, lookup));
        pack.addProvider(c -> new MBItemTagProvider(c, lookup, block.contentsGetter(), file));
    }

}
