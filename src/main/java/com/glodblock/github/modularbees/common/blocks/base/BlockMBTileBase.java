package com.glodblock.github.modularbees.common.blocks.base;

import com.glodblock.github.modularbees.common.tileentities.base.TileMBBase;
import com.glodblock.github.modularbees.util.ClientTickTile;
import com.glodblock.github.modularbees.util.GameUtil;
import com.glodblock.github.modularbees.util.ServerTickTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class BlockMBTileBase<T extends TileMBBase> extends BlockMBBase implements EntityBlock {

    private Class<T> tileEntityClass;

    private BlockEntityType<T> tileEntityType;

    @Nullable
    private BlockEntityTicker<T> serverTicker;

    @Nullable
    private BlockEntityTicker<T> clientTicker;

    public BlockMBTileBase(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public void bindTileEntity(Class<T> tileEntityClass, BlockEntityType<T> tileEntityType, BlockEntityTicker<T> serverTicker, BlockEntityTicker<T> clientTicker) {
        this.tileEntityClass = tileEntityClass;
        this.tileEntityType = tileEntityType;
        this.serverTicker = serverTicker;
        this.clientTicker = clientTicker;
    }

    public void bindTileEntity(Class<T> clazz, BlockEntityType<T> type) {
        this.tileEntityClass = clazz;
        this.tileEntityType = type;
        if (ServerTickTile.class.isAssignableFrom(clazz)) {
            this.serverTicker = (level, pos, state, entity) -> ((ServerTickTile) entity).tickServer(level, state);
        }
        if (ClientTickTile.class.isAssignableFrom(clazz)) {
            this.clientTicker = (level, pos, state, entity) -> ((ClientTickTile) entity).tickClient(level, state);
        }
    }

    @Nullable
    public T getBlockEntity(BlockGetter level, int x, int y, int z) {
        return this.getBlockEntity(level, new BlockPos(x, y, z));
    }

    @Nullable
    public T getBlockEntity(BlockGetter level, BlockPos pos) {
        final BlockEntity te = level.getBlockEntity(pos);
        if (this.tileEntityClass != null && this.tileEntityClass.isInstance(te)) {
            return this.tileEntityClass.cast(te);
        }
        return null;
    }

    public BlockEntityType<T> getBlockEntityType() {
        return this.tileEntityType;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return this.tileEntityType.create(pos, state);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <E extends BlockEntity> BlockEntityTicker<E> getTicker(Level level, @NotNull BlockState blockState, @NotNull BlockEntityType<E> type) {
        return (BlockEntityTicker<E>) (level.isClientSide() ? this.clientTicker : this.serverTicker);
    }

    @Override
    public void onRemove(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        // Drop internal content
        if (!level.isClientSide() && !newState.is(state.getBlock())) {
            var be = this.getBlockEntity(level, pos);
            if (be != null) {
                var drops = new ArrayList<ItemStack>();
                be.addInventoryDrops(level, pos, drops);
                GameUtil.spawnDrops(level, pos, drops);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public boolean triggerEvent(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, level, pos, eventID, eventParam);
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventID, eventParam);
    }

    public final BlockState getTileEntityBlockState(BlockState current, BlockEntity te) {
        if (current.getBlock() != this || !this.tileEntityClass.isInstance(te)) {
            return current;
        }
        return updateBlockStateFromTileEntity(current, this.tileEntityClass.cast(te));
    }

    protected BlockState updateBlockStateFromTileEntity(BlockState currentState, T te) {
        return currentState;
    }

}
