package com.glodblock.github.modularbees.common.tileentities.hive;

import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.common.MBConfig;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.caps.FluidHandlerHost;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.util.ServerTickTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class TileBeehiveExport extends TileBeehivePart implements ServerTickTile, ItemHandlerHost, FluidHandlerHost {

    private static final int AUTO_EXPORT_INTERVAL = MBConfig.AUTO_EXPORT_INTERVAL.get();
    private final IItemHandler item;
    private final IFluidHandler fluid;
    private BlockCapabilityCache<IItemHandler, Direction> itemCache;
    private BlockCapabilityCache<IFluidHandler, Direction> fluidCache;
    private long tick = 0;

    public TileBeehiveExport(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileBeehiveExport.class, TileBeehiveExport::new, MBSingletons.MODULAR_EXPORT), pos, state);
        this.item = new ProxyItemInventory(this::getHostItemInventory);
        this.fluid = new ProxyFluidInventory(this::getHostFluidInventory);
    }

    private IItemHandler getHostItemInventory() {
        if (this.isActive() && this.core instanceof TileModularBeehive beehive) {
            return beehive.getItemInventory();
        } else {
            return EmptyItemHandler.INSTANCE;
        }
    }

    private IFluidHandler getHostFluidInventory() {
        if (this.isActive() && this.core instanceof TileModularBeehive beehive) {
            return beehive.getFluidInventory();
        } else {
            return EmptyFluidHandler.INSTANCE;
        }
    }

    @Override
    public IItemHandler getItemInventory() {
        return this.item;
    }

    @Override
    public IFluidHandler getFluidInventory() {
        return this.fluid;
    }

    @Override
    public void tickServer(Level world, BlockState state) {
        if (this.isActive()) {
            this.tick++;
            if (this.tick % AUTO_EXPORT_INTERVAL == 0) {
                var facing = this.getFacing();
                if (this.itemCache == null) {
                    this.itemCache = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, (ServerLevel) world, this.getBlockPos().relative(facing), facing.getOpposite());
                }
                if (this.fluidCache == null) {
                    this.fluidCache = BlockCapabilityCache.create(Capabilities.FluidHandler.BLOCK, (ServerLevel) world, this.getBlockPos().relative(facing), facing.getOpposite());
                }
                var itemTarget = this.itemCache.getCapability();
                if (itemTarget != null) {
                    for (int x = 0; x < this.item.getSlots(); x ++) {
                        var stack = this.item.getStackInSlot(x);
                        var stored = stack.copy();
                        for (int y = 0; y < itemTarget.getSlots(); y ++) {
                            if (stack.isEmpty()) {
                                break;
                            }
                            stack = itemTarget.insertItem(y, stack, false);
                        }
                        this.item.extractItem(x, stored.getCount() - stack.getCount(), false);
                    }
                }
                var fluidTarget = this.fluidCache.getCapability();
                if (fluidTarget != null) {
                    var stack = this.fluid.getFluidInTank(0);
                    var filled = fluidTarget.fill(stack, IFluidHandler.FluidAction.EXECUTE);
                    this.fluid.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                }
            }
        }
    }

    public Direction getFacing() {
        return MBSingletons.MODULAR_EXPORT.getFacing(this.getBlockState());
    }

    private record ProxyItemInventory(Supplier<IItemHandler> getter) implements IItemHandler {

        @Override
        public int getSlots() {
            return this.getter.get().getSlots();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return this.getter.get().getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return this.getter.get().insertItem(slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return this.getter.get().extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return this.getter.get().getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return this.getter.get().isItemValid(slot, stack);
        }

    }

    private record ProxyFluidInventory(Supplier<IFluidHandler> getter) implements IFluidHandler {

        @Override
        public int getTanks() {
            return this.getter.get().getTanks();
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return this.getter.get().getFluidInTank(tank);
        }

        @Override
        public int getTankCapacity(int tank) {
            return this.getter.get().getTankCapacity(tank);
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return this.getter.get().isFluidValid(tank, stack);
        }

        @Override
        public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
            return this.getter.get().fill(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
            return this.getter.get().drain(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
            return this.getter.get().drain(maxDrain, action);
        }
    }

}
