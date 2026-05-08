package com.glodblock.github.modularbees.xmod.ae.tileentities;

import appeng.api.networking.GridFlags;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.storage.MEStorage;
import appeng.api.util.AECableType;
import appeng.hooks.ticking.TickHandler;
import appeng.me.helpers.BlockEntityNodeListener;
import appeng.me.helpers.IGridConnectedBlockEntity;
import com.glodblock.github.modularbees.common.tileentities.base.TileMBModularComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class TileAENetworkHost extends TileMBModularComponent implements IGridConnectedBlockEntity {

    private final IManagedGridNode mainNode = createMainNode()
            .setVisualRepresentation(this.getRepresentativeItem())
            .setInWorldNode(true)
            .setTagName("modular_bee_proxy");
    private final IActionSource source = IActionSource.ofMachine(this);
    private boolean setChangedQueued = false;

	public TileAENetworkHost(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.getMainNode().setFlags()
                .setIdlePowerUsage(1)
                .setFlags(GridFlags.REQUIRE_CHANNEL);
    }

    @Nullable
    public MEStorage getMEStorage() {
        var node = this.mainNode.getNode();
        if (node != null) {
            return node.getGrid().getService(IStorageService.class).getInventory();
        }
        return null;
    }

    public IActionSource getSource() {
        return this.source;
    }

    @Override
    public IManagedGridNode getMainNode() {
        return this.mainNode;
    }

    @Override
    public void saveChanges() {
        if (this.level == null) {
            return;
        }
        if (this.level.isClientSide) {
            this.setChanged();
        } else {
            this.level.blockEntityChanged(this.worldPosition);
            if (!this.setChangedQueued) {
                TickHandler.instance().addCallable(null, this::setChangedAtEndOfTick);
                this.setChangedQueued = true;
            }
        }
    }

    private void setChangedAtEndOfTick(Level level) {
        this.setChanged();
        this.setChangedQueued = false;
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.SMART;
    }

    @Override
    public void saveTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.saveTag(data, provider);
        this.mainNode.saveToNBT(data);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.loadTag(data, provider);
        this.mainNode.loadFromNBT(data);
    }

    protected IManagedGridNode createMainNode() {
        return GridHelper.createManagedNode(this, BlockEntityNodeListener.INSTANCE);
    }

    protected void scheduleInit() {
        GridHelper.onFirstTick(this, TileAENetworkHost::onReady);
    }

    public void onReady() {
        this.getMainNode().create(getLevel(), this.getBlockPos());
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        this.getMainNode().destroy();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.getMainNode().destroy();
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        scheduleInit();
    }

    public final void onGridConnectableSidesChanged() {
        getMainNode().setExposedOnSides(Set.of(this.getFacing()));
    }

    protected abstract Direction getFacing();

    protected abstract ItemLike getRepresentativeItem();

}
