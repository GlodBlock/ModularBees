package com.glodblock.github.modularbees.common.tileentities.base;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.common.blocks.base.BlockMBTileBase;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TileMBBase extends BlockEntity {

    public TileMBBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void addInventoryDrops(Level level, @NotNull BlockPos pos, List<ItemStack> drops) {

    }

    public boolean notLoaded() {
        return !this.level.hasChunkAt(this.worldPosition);
    }

    public final GlobalPos getGlobalPos() {
        if (this.level == null) {
            throw new IllegalStateException("Block entity is not in a level");
        }
        return GlobalPos.of(this.level.dimension(), getBlockPos());
    }

    public boolean isClient() {
        if (this.level == null) {
            return false;
        }
        return this.level.isClientSide;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public final void loadAdditional(CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        if (tag.contains("#upd", 7) && tag.size() == 1) {
            byte[] updateData = tag.getByteArray("#upd");
            if (this.readUpdateData(new FriendlyByteBuf(Unpooled.wrappedBuffer(updateData))) && this.level != null) {
                this.requestModelDataUpdate();
                this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 0);
            }
        } else {
            super.loadAdditional(tag, provider);
            this.loadTag(tag, provider);
        }
    }

    @Override
    public final void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        this.saveTag(tag, provider);
    }

    public void saveTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {

    }

    public void loadTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {

    }

    protected boolean readFromStream(FriendlyByteBuf data) {
        return false;
    }

    protected void writeToStream(FriendlyByteBuf data) {
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider provider) {
        CompoundTag data = new CompoundTag();
        FriendlyByteBuf stream = new FriendlyByteBuf(Unpooled.buffer());
        this.writeToStream(stream);
        stream.capacity(stream.readableBytes());
        data.putByteArray("#upd", stream.array());
        return data;
    }

    private boolean readUpdateData(FriendlyByteBuf stream) {
        boolean output = false;
        try {
            output = this.readFromStream(stream);
        } catch (Throwable err) {
            ModularBees.LOGGER.warn("Fail to read client update data.", err);
        }
        return output;
    }

    @Nullable
    public MBItemInventory getHandlerByName(String name) {
        return null;
    }

    public void markForUpdate() {
        this.requestModelDataUpdate();
        if (this.level != null && !this.isRemoved() && !notLoaded()) {
            boolean alreadyUpdated = false;
            BlockState currentState = getBlockState();
            if (currentState.getBlock() instanceof BlockMBTileBase<?> block) {
                BlockState newState = block.getTileEntityBlockState(currentState, this);
                if (currentState != newState) {
                    this.level.setBlockAndUpdate(worldPosition, newState);
                    alreadyUpdated = true;
                }
            }
            if (!alreadyUpdated) {
                this.level.sendBlockUpdated(this.worldPosition, currentState, currentState, Block.UPDATE_NEIGHBORS);
            }
        }
    }

    public void fastUpdate() {
        if (this.level != null && !this.isRemoved() && !notLoaded()) {
            BlockState currentState = getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, currentState, currentState, Block.UPDATE_NEIGHBORS);
        }
    }

}
