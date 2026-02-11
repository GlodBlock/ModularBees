package com.glodblock.github.modularbees.common.tileentities.centrifuge;

import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.common.MBConfig;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.common.inventory.SlotListener;
import com.glodblock.github.modularbees.util.ServerTickTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TileCentrifugeImport extends TileCentrifugePart implements ServerTickTile, ItemHandlerHost, SlotListener {

    private static final int AUTO_IMPORT_INTERVAL = MBConfig.AUTO_IMPORT_INTERVAL.get();
    private final MBItemInventory inputs = new MBItemInventory(this, 9).setFilter(this::validInput).inputOnly();
    private BlockCapabilityCache<IItemHandler, Direction> itemCache;
    private long tick = 0;

    public TileCentrifugeImport(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileCentrifugeImport.class, TileCentrifugeImport::new, MBSingletons.MODULAR_CENTRIFUGE_IMPORT), pos, state);
    }

    public void onFacingChange() {
        this.itemCache = null;
        this.invalidateCapabilities();
    }

    @Override
    public void onChange(IItemHandler inv, int slot) {
        if (inv == this.inputs && this.isActive() && this.core instanceof TileModularCentrifuge centrifuge) {
            centrifuge.unblock();
        }
    }

    @Nullable
    public MBItemInventory getHandlerByName(String name) {
        return this.inputs;
    }

    private boolean validInput(ItemStack stack) {
        if (this.isActive() && this.core instanceof TileModularCentrifuge centrifuge) {
            return centrifuge.validInput(stack);
        }
        return false;
    }

    @Override
    public void addInventoryDrops(Level level, @NotNull BlockPos pos, List<ItemStack> drops) {
        drops.addAll(this.inputs.toList());
    }

    @Override
    public void saveTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.saveTag(data, provider);
        data.put("inputs", this.inputs.serializeNBT(provider));
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.loadTag(data, provider);
        this.inputs.deserializeNBT(provider, data.getCompound("inputs"));
    }

    @Override
    public MBItemInventory getItemInventory() {
        return this.inputs;
    }

    @Override
    public void tickServer(Level world, BlockState state) {
        if (this.isActive()) {
            this.tick++;
            if (this.tick % AUTO_IMPORT_INTERVAL == 0) {
                var facing = this.getFacing();
                if (this.itemCache == null) {
                    this.itemCache = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, (ServerLevel) world, this.getBlockPos().relative(facing), facing.getOpposite());
                }
                var itemTarget = this.itemCache.getCapability();
                if (itemTarget != null) {
                    for (int x = 0; x < itemTarget.getSlots(); ++x) {
                        var stack = itemTarget.getStackInSlot(x);
                        var stored = stack.copy();
                        for (int y = 0; y < this.inputs.getSlots(); ++y) {
                            if (stack.isEmpty()) {
                                break;
                            }
                            stack = this.inputs.insertItem(y, stack, false);
                        }
                        itemTarget.extractItem(x, stored.getCount() - stack.getCount(), false);
                    }
                }
            }
        }
    }

    public Direction getFacing() {
        return MBSingletons.MODULAR_CENTRIFUGE_IMPORT.getFacing(this.getBlockState());
    }

}
