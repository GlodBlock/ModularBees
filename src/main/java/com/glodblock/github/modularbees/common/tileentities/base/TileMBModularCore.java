package com.glodblock.github.modularbees.common.tileentities.base;

import com.glodblock.github.modularbees.util.ServerTickTile;
import com.glodblock.github.modularbees.util.StructureListener;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class TileMBModularCore extends TileMBBase implements ServerTickTile {

    private final List<TileMBModularComponent> components = new ArrayList<>();
    private final StructureListener listener;
    private boolean refresh = true;
    private boolean formed = false;

    public TileMBModularCore(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.listener = new StructureListener(this);
    }

    @Override
    public void onChunkUnloaded() {
        this.listener.invalidate();
        super.onChunkUnloaded();
    }

    public abstract boolean isStructurePos(BlockPos pos);

    public abstract boolean isStructurePos(ChunkPos pos);

    protected abstract boolean buildStructure(Consumer<TileMBModularComponent> collector, Level world);

    protected abstract void logicTick(@NotNull Level world, BlockState state, List<TileMBModularComponent> components);

    public void onStateChange() {
        this.formed = false;
        this.refresh = true;
        this.unlink();
    }

    public void onStructureChange() {
        if (this.isRemoved()) {
            return;
        }
        this.refresh = true;
    }

    public boolean isFormed() {
        return this.formed;
    }

    public void formStructure() {
        this.unlink();
        this.formed = false;
        this.formed = this.buildStructure(c -> {
            this.components.add(c);
            c.linkCore(this);
        }, this.level);
        this.refresh = false;
    }

    private void unlink() {
        this.components.forEach(c -> c.linkCore(null));
        this.components.clear();
    }

    @Override
    public void tickServer(Level world, BlockState state) {
        if (this.refresh) {
            this.formStructure();
        }
        if (this.formed) {
            this.logicTick(world, state, this.components);
        }
    }

    protected <T extends TileMBModularComponent> List<T> getComponents(Class<T> type) {
        if (!this.formed) {
            return List.of();
        }
        return this.components.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }

}
