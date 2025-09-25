package com.glodblock.github.modularbees.container;

import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.common.tileentities.base.TileMBBase;
import com.glodblock.github.modularbees.container.slot.DisplaySlot;
import com.glodblock.github.modularbees.container.slot.MBInventorySlot;
import com.glodblock.github.modularbees.network.MBNetworkHandler;
import com.glodblock.github.modularbees.network.SMBGenericPacket;
import com.glodblock.github.modularbees.util.ContainerResolver;
import com.glodblock.github.modularbees.util.DirtyFieldMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class ContainerMBBase<T extends TileMBBase> extends AbstractContainerMenu implements IActionHolder {

    private final T host;
    private final Inventory playerInventory;
    private final DirtyFieldMap syncMap = new DirtyFieldMap();
    private ContainerResolver resolver;
    private boolean valid = true;
    private final ActionMap actions = ActionMap.create();

    protected ContainerMBBase(@Nullable MenuType<?> type, int id, Inventory inv, T host) {
        super(type, id);
        this.host = host;
        this.playerInventory = inv;
    }

    public void setResolver(ContainerResolver resolver) {
        this.resolver = resolver;
    }

    protected ContainerResolver getResolver() {
        return this.resolver;
    }

    public T getHost() {
        return this.host;
    }

    public void invalidate() {
        this.valid = false;
    }

    protected DirtyFieldMap getSync() {
        return this.syncMap;
    }

    public void receiveUpdate(RegistryFriendlyByteBuf buf) {
        this.syncMap.fieldSync(buf);
    }

    protected final void bindPlayerInventorySlots(Inventory inventory) {
        for (int i = 0; i < inventory.items.size(); i++) {
            int x = i % 9;
            int y = i / 9;
            int hotBarOffset = i < Inventory.getSelectionSize() ? 0 : 4;
            var slot = new Slot(inventory, i, x * 18 + this.playerLeftOffset(), -y * 18 - hotBarOffset + this.getHeight() - this.playerBottomOffset());
            this.addSlot(slot);
        }
    }

    protected void addItemHandlerSlot(@Nullable IItemHandler handler, int posX, int posY, int columns) {
        if (handler == null) {
            return;
        }
        for (int index = 0; index < handler.getSlots(); index ++) {
            int x = index % columns;
            int y = index / columns;
            if (handler instanceof MBItemInventory inv) {
                this.addSlot(new MBInventorySlot(inv, index, posX + x * 18, posY + y * 18));
            } else {
                this.addSlot(new SlotItemHandler(handler, index, posX + x * 18, posY + y * 18));
            }
        }
    }

    protected Slot addSlot(@Nullable IItemHandler handler, int index, int posX, int posY) {
        if (handler == null) {
            return null;
        }
        if (handler instanceof MBItemInventory inv) {
            return this.addSlot(new MBInventorySlot(inv, index, posX, posY));
        }
        return this.addSlot(new SlotItemHandler(handler, index, posX, posY));
    }

    protected int playerBottomOffset() {
        return 24;
    }

    protected int playerLeftOffset() {
        return 8;
    }

    abstract int getHeight();

    abstract int getWidth();

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();
        if (!this.syncMap.isEmpty() && this.getPlayer() instanceof ServerPlayer player) {
            MBNetworkHandler.INSTANCE.sendTo(this.syncMap.sendFullPacket(this.containerId), player);
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int idx) {
        if (this.isClientSide()) {
            return ItemStack.EMPTY;
        }
        var clickSlot = this.slots.get(idx);
        if (!clickSlot.mayPickup(player)) {
            return ItemStack.EMPTY;
        }
        var stackToMove = clickSlot.getItem();
        if (stackToMove.isEmpty()) {
            return ItemStack.EMPTY;
        }
        stackToMove = clickSlot.getItem();
        if (stackToMove.isEmpty()) {
            return ItemStack.EMPTY;
        }
        var originalStackToMove = stackToMove.copy();
        stackToMove = quickMoveToOtherSlots(stackToMove, isPlayerSideSlot(clickSlot));
        if (!ItemStack.matches(originalStackToMove, stackToMove)) {
            clickSlot.setByPlayer(stackToMove.isEmpty() ? ItemStack.EMPTY : stackToMove);
        }
        return ItemStack.EMPTY;
    }

    private ItemStack quickMoveToOtherSlots(ItemStack stackToMove, boolean fromPlayerSide) {
        var destinationSlots = getQuickMoveDestinationSlots(stackToMove, fromPlayerSide);
        if (destinationSlots.isEmpty() && fromPlayerSide) {
            for (Slot cs : this.slots) {
                if (cs instanceof DisplaySlot && !isPlayerSideSlot(cs)) {
                    var destination = cs.getItem();
                    if (ItemStack.isSameItemSameComponents(destination, stackToMove)) {
                        break;
                    } else if (destination.isEmpty()) {
                        cs.set(stackToMove.copy());
                        this.broadcastChanges();
                        break;
                    }
                }
            }
            return stackToMove;
        }
        for (var dest : destinationSlots) {
            if (dest.hasItem() && (stackToMove = dest.safeInsert(stackToMove)).isEmpty()) {
                return stackToMove;
            }
        }
        for (var dest : destinationSlots) {
            if (!dest.hasItem() && (stackToMove = dest.safeInsert(stackToMove)).isEmpty()) {
                return stackToMove;
            }
        }
        return stackToMove;
    }

    protected List<Slot> getQuickMoveDestinationSlots(ItemStack stackToMove, boolean fromPlayerSide) {
        var destinationSlots = new ArrayList<Slot>();
        for (var candidateSlot : this.slots) {
            if (isValidQuickMoveDestination(candidateSlot, stackToMove, fromPlayerSide)) {
                destinationSlots.add(candidateSlot);
            }
        }
        destinationSlots.sort(Comparator.comparingInt(this::getSlotSortIndex));
        return destinationSlots;
    }

    protected int getSlotSortIndex(Slot slot) {
        return slot.getSlotIndex();
    }

    /**
     * Check if a given candidate slot is a valid destination for {@link #quickMoveStack}.
     */
    protected boolean isValidQuickMoveDestination(Slot candidateSlot, ItemStack stackToMove,
                                                  boolean fromPlayerSide) {
        return isPlayerSideSlot(candidateSlot) != fromPlayerSide
                && !(candidateSlot instanceof DisplaySlot)
                && candidateSlot.mayPlace(stackToMove);
    }

    protected boolean isPlayerSideSlot(Slot slot) {
        return slot.container == this.playerInventory;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.valid;
    }

    @Override
    public void broadcastChanges() {
        if (!this.valid) {
            return;
        }
        if (this.isServerSide()) {
            if (this.host.getLevel().getBlockEntity(this.host.getBlockPos()) != this.host) {
                this.invalidate();
            }
            if (this.syncMap.needSync() && this.getPlayer() instanceof ServerPlayer player) {
                MBNetworkHandler.INSTANCE.sendTo(this.syncMap.sendDeltaPacket(this.containerId), player);
            }
        }
        super.broadcastChanges();
    }

    public boolean isClientSide() {
        return this.getPlayer().getCommandSenderWorld().isClientSide();
    }

    public boolean isServerSide() {
        return !this.isClientSide();
    }

    public Player getPlayer() {
        return this.getPlayerInventory().player;
    }

    public Inventory getPlayerInventory() {
        return this.playerInventory;
    }

    public void sendAction(String id, Object... paras) {
        if (this.getPlayer() instanceof ServerPlayer player) {
            MBNetworkHandler.INSTANCE.sendTo(new SMBGenericPacket(id, paras), player);
        }
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }

}
