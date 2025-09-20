package com.glodblock.github.modularbees.common;

import com.glodblock.github.glodium.registry.RegistryHandler;
import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.common.blocks.base.BlockMBTileBase;
import com.glodblock.github.modularbees.common.caps.EnergyHandlerHost;
import com.glodblock.github.modularbees.common.caps.FluidHandlerHost;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.items.ItemMBBlock;
import com.glodblock.github.modularbees.common.recipe.TreaterRecipe;
import com.glodblock.github.modularbees.common.tileentities.base.TileMBBase;
import com.glodblock.github.modularbees.container.ContainerMBAlveary;
import com.glodblock.github.modularbees.container.ContainerMBFeeder;
import com.glodblock.github.modularbees.container.ContainerMBModularBeehive;
import com.glodblock.github.modularbees.container.ContainerMBOverclocker;
import com.glodblock.github.modularbees.container.ContainerMBTreater;
import com.glodblock.github.modularbees.container.MBGuiHandler;
import com.glodblock.github.modularbees.util.RegisterTask;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MBRegistryHandler extends RegistryHandler {

    public static final MBRegistryHandler INSTANCE = new MBRegistryHandler();

    public MBRegistryHandler() {
        super(ModularBees.MODID);
        this.cap(ItemHandlerHost.class, Capabilities.ItemHandler.BLOCK, ItemHandlerHost::getItemInventory);
        this.cap(FluidHandlerHost.class, Capabilities.FluidHandler.BLOCK, FluidHandlerHost::getFluidInventory);
        this.cap(EnergyHandlerHost.class, Capabilities.EnergyStorage.BLOCK, EnergyHandlerHost::getEnergyStorage);
    }

    public <T extends TileMBBase> void block(String name, BlockMBTileBase<T> block, Class<T> clazz, BlockEntityType.BlockEntitySupplier<? extends T> supplier) {
        block.bindTileEntity(clazz, GlodUtil.getTileType(clazz, supplier, block));
        block(name, block, b -> new ItemMBBlock(b, new Item.Properties()));
        tile(name, block.getBlockEntityType());
    }

    public void init() {

    }

    @Override
    protected void onRegisterBlocks() {
        super.onRegisterBlocks();
        this.blocks.forEach(e -> {
            if (e.getRight() instanceof RegisterTask task) {
                task.onRegister(ModularBees.id(e.getLeft()));
            }
        });
    }

    @Override
    protected void onRegisterItems() {
        super.onRegisterItems();
        this.items.forEach(e -> {
            if (e.getRight() instanceof RegisterTask task) {
                task.onRegister(ModularBees.id(e.getLeft()));
            }
        });
    }

    private void registerContainer() {
        MBGuiHandler.registerResolver(MBGuiHandler.TileResolver::new);
        ContainerMBModularBeehive.TYPE.register();
        ContainerMBAlveary.TYPE.register();
        ContainerMBFeeder.TYPE.register();
        ContainerMBOverclocker.TYPE.register();
        ContainerMBTreater.TYPE.register();
    }

    private void registerRecipe() {
        Registry.register(BuiltInRegistries.RECIPE_TYPE, TreaterRecipe.ID, TreaterRecipe.TYPE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, TreaterRecipe.ID, TreaterRecipe.SERIALIZER);
    }

    @Override
    public void runRegister() {
        super.runRegister();
        this.registerContainer();
        this.registerRecipe();
    }

    @SubscribeEvent
    @Override
    public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        super.onRegisterCapabilities(event);
    }

    public Collection<ItemLike> getItems() {
        return Stream.concat(this.items.stream(), this.blocks.stream()).map(Pair::getRight).collect(Collectors.toList());
    }

    public Collection<Block> getBlocks() {
        return this.blocks.stream().map(Pair::getRight).collect(Collectors.toList());
    }

    public void registerTab(Registry<CreativeModeTab> registry) {
        var tab = CreativeModeTab.builder()
                .icon(() -> new ItemStack(MBSingletons.MODULAR_BEEHIVE_CORE))
                .title(Component.translatable("itemGroup.mb"))
                .displayItems((p, o) -> {
                    for (Pair<String, Item> entry : this.items) {
                        o.accept(entry.getRight());
                    }
                    for (Pair<String, Block> entry : this.blocks) {
                        o.accept(entry.getRight());
                    }
                })
                .build();
        Registry.register(registry, ModularBees.id("tab_main"), tab);
    }

}
