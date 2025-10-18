package com.glodblock.github.modularbees.common.tileentities.centrifuge;

import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.common.MBConfig;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.caps.FluidHandlerHost;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.inventory.ProxyFluidInventory;
import com.glodblock.github.modularbees.common.inventory.ProxyItemInventory;
import com.glodblock.github.modularbees.common.inventory.RandomAccessTank;
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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

import java.util.ArrayList;
import java.util.List;

public class TileCentrifugeExport extends TileCentrifugePart implements ServerTickTile, ItemHandlerHost, FluidHandlerHost {

    private static final int AUTO_EXPORT_INTERVAL = MBConfig.AUTO_EXPORT_INTERVAL.get();
    private static final List<ItemStack> EMPTY_ITEMS = new ArrayList<>();
    private static final List<FluidStack> EMPTY_FLUIDS = new ArrayList<>();
    private final IItemHandler item;
    private final IFluidHandler fluid;
    private BlockCapabilityCache<IItemHandler, Direction> itemCache;
    private BlockCapabilityCache<IFluidHandler, Direction> fluidCache;
    private long tick = 0;

    public TileCentrifugeExport(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileCentrifugeExport.class, TileCentrifugeExport::new, MBSingletons.MODULAR_CENTRIFUGE_EXPORT), pos, state);
        this.item = new ProxyItemInventory(this::getHostItemInventory);
        this.fluid = new ProxyFluidInventory(this::getHostFluidInventory);
    }

    private List<ItemStack> getSendingList() {
        if (this.isActive() && this.core instanceof TileModularCentrifuge centrifuge) {
            return centrifuge.getSending();
        } else {
            return EMPTY_ITEMS;
        }
    }

    private List<FluidStack> getFillingList() {
        if (this.isActive() && this.core instanceof TileModularCentrifuge centrifuge) {
            return centrifuge.getFilling();
        } else {
            return EMPTY_FLUIDS;
        }
    }

    private IItemHandler getHostItemInventory() {
        if (this.isActive() && this.core instanceof TileModularCentrifuge centrifuge) {
            return centrifuge.getHandlerByName("outputs");
        } else {
            return EmptyItemHandler.INSTANCE;
        }
    }

    private RandomAccessTank getHostFluidInventory() {
        if (this.isActive() && this.core instanceof TileModularCentrifuge beehive) {
            return beehive.getFluidInventory();
        } else {
            return RandomAccessTank.EMPTY;
        }
    }

    public void onFacingChange() {
        this.itemCache = null;
        this.fluidCache = null;
        this.invalidateCapabilities();
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
                    var sending = this.getSendingList();
                    for (int i = 0; i < sending.size(); ++i) {
                        var stack = sending.get(i).copy();
                        for (int y = 0; y < itemTarget.getSlots(); y ++) {
                            if (stack.isEmpty()) {
                                break;
                            }
                            stack = itemTarget.insertItem(y, stack, false);
                        }
                        sending.set(i, stack);
                    }
                    sending.removeIf(ItemStack::isEmpty);
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
                    var filling = this.getFillingList();
                    for (int i = 0; i < filling.size(); ++i) {
                        var stack = filling.get(i).copy();
                        if (stack.isEmpty()) {
                            continue;
                        }
                        var filled = fluidTarget.fill(stack, IFluidHandler.FluidAction.EXECUTE);
                        stack.shrink(filled);
                        filling.set(i, stack);
                    }
                    filling.removeIf(FluidStack::isEmpty);
                    var racTank = this.getHostFluidInventory();
                    if (racTank != RandomAccessTank.EMPTY) {
                        for (int x = 0; x < racTank.getTanks(); x ++) {
                            var stack = racTank.getFluidInTank(x);
                            if (stack.isEmpty()) {
                                continue;
                            }
                            var filled = fluidTarget.fill(stack, IFluidHandler.FluidAction.EXECUTE);
                            racTank.getTank(x).drain(filled, IFluidHandler.FluidAction.EXECUTE);
                        }
                        var stack = this.fluid.getFluidInTank(0);
                        var filled = fluidTarget.fill(stack, IFluidHandler.FluidAction.EXECUTE);
                        this.fluid.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                    }
                }
            }
        }
    }

    public Direction getFacing() {
        return MBSingletons.MODULAR_CENTRIFUGE_EXPORT.getFacing(this.getBlockState());
    }

}
