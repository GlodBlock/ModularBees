package com.glodblock.github.modularbees.common.tileentities.hive;

import com.glodblock.github.modularbees.common.MBConfig;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.caps.FluidHandlerHost;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.util.GameUtil;
import com.glodblock.github.modularbees.util.ServerTickTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.EmptyResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;

public class TileBeehiveExport extends TileBeehivePart implements ServerTickTile, ItemHandlerHost, FluidHandlerHost {

    private static final int AUTO_EXPORT_INTERVAL = MBConfig.AUTO_EXPORT_INTERVAL.get();
    private final ResourceHandler<@NotNull ItemResource> item;
    private final ResourceHandler<@NotNull FluidResource> fluid;
    private BlockCapabilityCache<@NotNull ResourceHandler<@NotNull ItemResource>, Direction> itemCache;
    private BlockCapabilityCache<@NotNull ResourceHandler<@NotNull FluidResource>, Direction> fluidCache;
    private long tick = 0;

    public TileBeehiveExport(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.item = new DelegatingResourceHandler<>(this::getHostItemInventory);
        this.fluid = new DelegatingResourceHandler<>(this::getHostFluidInventory);
    }

    private ResourceHandler<@NotNull ItemResource> getHostItemInventory() {
        if (this.isActive() && this.core instanceof TileModularBeehive beehive) {
            return beehive.getItemInventory();
        } else {
            return EmptyResourceHandler.instance();
        }
    }

    private ResourceHandler<@NotNull FluidResource> getHostFluidInventory() {
        if (this.isActive() && this.core instanceof TileModularBeehive beehive) {
            return beehive.getFluidInventory();
        } else {
            return EmptyResourceHandler.instance();
        }
    }

    public void onFacingChange() {
        this.itemCache = null;
        this.fluidCache = null;
        this.invalidateCapabilities();
    }

    @Override
    public ResourceHandler<@NotNull ItemResource> getItemInventory() {
        return this.item;
    }

    @Override
    public ResourceHandler<@NotNull FluidResource> getFluidInventory() {
        return this.fluid;
    }

    @Override
    public void tickServer(Level world, BlockState state) {
        if (this.isActive()) {
            this.tick++;
            if (this.tick % AUTO_EXPORT_INTERVAL == 0) {
                var facing = this.getFacing();
                if (this.itemCache == null) {
                    this.itemCache = BlockCapabilityCache.create(Capabilities.Item.BLOCK, (ServerLevel) world, this.getBlockPos().relative(facing), facing.getOpposite());
                }
                if (this.fluidCache == null) {
                    this.fluidCache = BlockCapabilityCache.create(Capabilities.Fluid.BLOCK, (ServerLevel) world, this.getBlockPos().relative(facing), facing.getOpposite());
                }
                var itemTarget = this.itemCache.getCapability();
                if (itemTarget != null) {
                    GameUtil.transferResource(this.item, itemTarget);
                }
                var fluidTarget = this.fluidCache.getCapability();
                if (fluidTarget != null) {
                    GameUtil.transferResource(this.fluid, fluidTarget);
                }
            }
        }
    }

    public Direction getFacing() {
        return MBSingletons.MODULAR_BEEHIVE_EXPORT.get().getFacing(this.getBlockState());
    }

}
