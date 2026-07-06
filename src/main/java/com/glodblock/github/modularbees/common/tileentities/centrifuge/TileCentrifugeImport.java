package com.glodblock.github.modularbees.common.tileentities.centrifuge;

import com.glodblock.github.modularbees.common.MBConfig;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.common.inventory.SlotListener;
import com.glodblock.github.modularbees.util.GameUtil;
import com.glodblock.github.modularbees.util.ServerTickTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TileCentrifugeImport extends TileCentrifugePart implements ServerTickTile, ItemHandlerHost, SlotListener {

    private static final int AUTO_IMPORT_INTERVAL = MBConfig.AUTO_IMPORT_INTERVAL.get();
    private final MBItemInventory inputs = new MBItemInventory(this, 9).setFilter(this::validInput).inputOnly();
    private BlockCapabilityCache<@NotNull ResourceHandler<@NotNull ItemResource>, Direction> itemCache;
    private long tick = 0;

    public TileCentrifugeImport(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void onFacingChange() {
        this.itemCache = null;
        this.invalidateCapabilities();
    }

    @Override
    public void onChange(ResourceHandler<@NotNull ItemResource> inv, int slot) {
        if (inv == this.inputs && this.isActive() && this.core instanceof TileModularCentrifuge centrifuge) {
            centrifuge.unblock();
        }
    }

    @Nullable
    public MBItemInventory getHandlerByName(String name) {
        return this.inputs;
    }

    private boolean validInput(ItemResource stack) {
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
    public void saveTag(ValueOutput data) {
        super.saveTag(data);
        this.inputs.serialize(data.child("inputs"));
    }

    @Override
    public void loadTag(ValueInput data) {
        super.loadTag(data);
        data.child("inputs").ifPresent(this.inputs::deserialize);
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
                    this.itemCache = BlockCapabilityCache.create(Capabilities.Item.BLOCK, (ServerLevel) world, this.getBlockPos().relative(facing), facing.getOpposite());
                }
                var itemTarget = this.itemCache.getCapability();
                if (itemTarget != null) {
                    GameUtil.transferResource(itemTarget, this.inputs);
                }
            }
        }
    }

    public Direction getFacing() {
        return MBSingletons.MODULAR_CENTRIFUGE_IMPORT.get().getFacing(this.getBlockState());
    }

}
