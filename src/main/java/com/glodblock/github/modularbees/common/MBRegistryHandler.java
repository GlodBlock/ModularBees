package com.glodblock.github.modularbees.common;

import com.glodblock.github.glodium.registry.RegistryHandler;
import com.glodblock.github.glodium.registry.defer.DeferredTileEntityType;
import com.glodblock.github.glodium.registry.token.TileToken;
import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.common.blocks.base.BlockMBTileBase;
import com.glodblock.github.modularbees.common.caps.EnergyHandlerHost;
import com.glodblock.github.modularbees.common.caps.FluidHandlerHost;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.fluids.FluidDragonBreath;
import com.glodblock.github.modularbees.common.recipe.ElectrodeRecipe;
import com.glodblock.github.modularbees.common.recipe.TreaterRecipe;
import com.glodblock.github.modularbees.common.tileentities.base.TileMBBase;
import com.glodblock.github.modularbees.container.ContainerMBAlveary;
import com.glodblock.github.modularbees.container.ContainerMBBeeExtractor;
import com.glodblock.github.modularbees.container.ContainerMBDragon;
import com.glodblock.github.modularbees.container.ContainerMBFeeder;
import com.glodblock.github.modularbees.container.ContainerMBGearbox;
import com.glodblock.github.modularbees.container.ContainerMBHeater;
import com.glodblock.github.modularbees.container.ContainerMBImport;
import com.glodblock.github.modularbees.container.ContainerMBModularBeehive;
import com.glodblock.github.modularbees.container.ContainerMBModularCentrifuge;
import com.glodblock.github.modularbees.container.ContainerMBOverclocker;
import com.glodblock.github.modularbees.container.ContainerMBTreater;
import com.glodblock.github.modularbees.container.base.MBGuiHandler;
import com.glodblock.github.modularbees.util.Hidden;
import com.glodblock.github.modularbees.util.RegisterTask;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MBRegistryHandler extends RegistryHandler {

    public static MBRegistryHandler INSTANCE;

    public MBRegistryHandler(IEventBus modBus) {
        super(ModularBees.MODID, modBus);
        modBus.addListener(this::runRegister);
        this.cap(ItemHandlerHost.class, Capabilities.Item.BLOCK, ItemHandlerHost::getItemInventory);
        this.cap(FluidHandlerHost.class, Capabilities.Fluid.BLOCK, FluidHandlerHost::getFluidInventory);
        this.cap(EnergyHandlerHost.class, Capabilities.Energy.BLOCK, EnergyHandlerHost::getEnergyStorage);
    }

    public <T extends TileMBBase, B extends BlockMBTileBase<T>> DeferredBlock<@NotNull B> block(String name, Function<BlockBehaviour.Properties, B> builder, Class<T> clazz, TileFactory<@NotNull T> supplier) {
        var block = this.block(name, builder, BlockBehaviour.Properties.of());
        this.tile(name, clazz, supplier, block);
        return block;
    }

    public <T extends TileMBBase> DeferredTileEntityType<T> tile(String name, Class<T> tileClass, TileFactory<@NotNull T> factory, DeferredBlock<? extends @NotNull BlockMBTileBase<T>> block) {
        return (DeferredTileEntityType<T>) this.tiles.register(name, () -> {
            AtomicReference<BlockEntityType<@NotNull T>> holder = new AtomicReference<>();
            BlockEntityType<@NotNull T> tileType = new BlockEntityType<>((pos, state) -> factory.create(holder.get(), pos, state), Set.of(block.get()));
            holder.set(tileType);
            block.get().bindTileEntity(tileClass, tileType);
            var token = new TileToken(holder::get, tileClass);
            this.tileTypes.add(token);
            this.tileBind.add(Pair.of(token, Set.of(block.get())));
            return tileType;
        });
    }

    public <T extends Block> DeferredBlock<@NotNull T> fluidBlock(String name, Function<BlockBehaviour.Properties, T> builder) {
        return this.blocks.register(name, key -> builder.apply(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, key))));
    }

    @Override
    public <T extends Block> DeferredBlock<@NotNull T> block(String name, Function<BlockBehaviour.Properties, T> builder, BlockBehaviour.Properties properties, BiFunction<Block, Item.Properties, Item> itemWrapper, Item.Properties itemProperties) {
        var block = this.blocks.register(name, key -> {
            var result = builder.apply(properties.setId(ResourceKey.create(Registries.BLOCK, key)));
            if (result instanceof RegisterTask task) {
                task.onRegister(ModularBees.id(name));
            }
            return result;
        });
        this.item(name, prop -> itemWrapper.apply(block.get(), prop), itemProperties);
        return block;
    }

    @Override
    public <T extends Item> DeferredItem<@NotNull T> item(String name, Function<Item.Properties, T> builder, Item.Properties properties) {
        return this.items.register(name, key -> {
            var result = builder.apply(properties.setId(ResourceKey.create(Registries.ITEM, key)));
            if (result instanceof RegisterTask task) {
                task.onRegister(ModularBees.id(name));
            }
            return result;
        });
    }

    private void onRegisterContainer() {
        MBGuiHandler.registerResolver(MBGuiHandler.TileResolver::new);
        ContainerMBModularBeehive.TYPE.register();
        ContainerMBAlveary.TYPE.register();
        ContainerMBFeeder.TYPE.register();
        ContainerMBOverclocker.TYPE.register();
        ContainerMBTreater.TYPE.register();
        ContainerMBDragon.TYPE.register();
        ContainerMBModularCentrifuge.TYPE.register();
        ContainerMBImport.TYPE.register();
        ContainerMBHeater.TYPE.register();
        ContainerMBGearbox.TYPE.register();
        ContainerMBBeeExtractor.TYPE.register();
    }

    private void onRegisterRecipeType() {
        Registry.register(BuiltInRegistries.RECIPE_TYPE, TreaterRecipe.ID, TreaterRecipe.TYPE);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, ElectrodeRecipe.ID, ElectrodeRecipe.TYPE);
    }

    private void onRegisterRecipeSerializer() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, TreaterRecipe.ID, TreaterRecipe.SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ElectrodeRecipe.ID, ElectrodeRecipe.SERIALIZER);
    }

    private void onRegisterFluidTypes() {
        Registry.register(NeoForgeRegistries.FLUID_TYPES, ModularBees.id("dragon_breath"), FluidDragonBreath.TYPE);
    }

    private void onRegisterFluid() {
        Registry.register(BuiltInRegistries.FLUID, ModularBees.id("dragon_breath"), FluidDragonBreath.getFluid());
        Registry.register(BuiltInRegistries.FLUID, ModularBees.id("dragon_breath_flow"), FluidDragonBreath.getFlowFluid());
    }

    public void runRegister(RegisterEvent e) {
        if (e.getRegistry().equals(BuiltInRegistries.MENU)) {
            this.onRegisterContainer();
        } else if (e.getRegistry().equals(BuiltInRegistries.RECIPE_TYPE)) {
            this.onRegisterRecipeType();
        } else if (e.getRegistry().equals(BuiltInRegistries.RECIPE_SERIALIZER)) {
            this.onRegisterRecipeSerializer();
        } else if (e.getRegistry().equals(BuiltInRegistries.FLUID)) {
            this.onRegisterFluid();
        } else if (e.getRegistry().equals(NeoForgeRegistries.FLUID_TYPES)) {
            this.onRegisterFluidTypes();
        }
    }

    public Collection<ItemLike> getItems() {
        return Stream.concat(this.items.getEntries().stream(), this.blocks.getEntries().stream()).map(DeferredHolder::get).collect(Collectors.toList());
    }

    public Collection<Block> getBlocks() {
        return this.blocks.getEntries().stream().map(DeferredHolder::get).filter(Hidden::visible).map(b -> (Block) b).toList();
    }

    public void registerTab(Registry<@NotNull CreativeModeTab> registry) {
        var tab = CreativeModeTab.builder()
                .icon(() -> new ItemStack(MBSingletons.MODULAR_BEEHIVE_CORE))
                .title(Component.translatable("itemGroup.mb"))
                .displayItems((p, o) -> {
                    for (var entry : this.items.getEntries()) {
                        o.accept(entry.get());
                    }
                    for (var entry : this.blocks.getEntries()) {
                        if (entry.get() instanceof Hidden) {
                            continue;
                        }
                        o.accept(entry.get());
                    }
                })
                .build();
        Registry.register(registry, ModularBees.id("tab_main"), tab);
    }

    public interface TileFactory<T extends BlockEntity> {

        T create(BlockEntityType<@NotNull T> type, BlockPos worldPosition, BlockState blockState);

    }

}
