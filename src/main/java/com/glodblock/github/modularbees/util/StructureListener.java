package com.glodblock.github.modularbees.util;

import com.glodblock.github.modularbees.common.tileentities.base.TileMBModularCore;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

public class StructureListener {

    private TileMBModularCore host;
    private boolean alive;

    public StructureListener(TileMBModularCore host) {
        this.host = host;
        this.alive = true;
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onUnload(final LevelEvent.Unload e) {
        if (this.host != null && this.host.getLevel() == e.getLevel()) {
            this.invalidate();
        }
    }

    @SubscribeEvent
    public void onBlockUpdate(final BlockEvent.NeighborNotifyEvent e) {
        if (this.host != null && this.host.getLevel() == e.getLevel()) {
            // The core updated
            if (e.getPos().equals(this.host.getBlockPos())) {
                this.host.onStateChange();
            }
            // The structure updated
            if (this.host.isStructurePos(e.getPos())) {
                this.host.onStructureChange();
            }
        }
    }

    @SubscribeEvent
    public void onChunkLoad(final ChunkEvent.Load e) {
        if (this.host != null && this.host.getLevel() == e.getLevel()) {
            if (this.host.isStructurePos(e.getChunk().getPos())) {
                this.host.onStructureChange();
            }
        }
    }

    @SubscribeEvent
    public void onChunkUnload(final ChunkEvent.Unload e) {
        if (this.host != null && this.host.getLevel() == e.getLevel()) {
            if (this.host.isStructurePos(e.getChunk().getPos())) {
                this.host.onStructureChange();
            }
        }
    }

    public void invalidate() {
        if (!this.alive) {
            return;
        }
        this.alive = false;
        this.host = null;
        NeoForge.EVENT_BUS.unregister(this);
    }

}
