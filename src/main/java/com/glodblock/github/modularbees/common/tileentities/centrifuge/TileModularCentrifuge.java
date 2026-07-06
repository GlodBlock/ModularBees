package com.glodblock.github.modularbees.common.tileentities.centrifuge;

import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.blocks.centrifuge.Centrifuge;
import com.glodblock.github.modularbees.common.caps.FluidHandlerHost;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.inventory.MBFluidInventory;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.common.inventory.SlotListener;
import com.glodblock.github.modularbees.common.inventory.TankListener;
import com.glodblock.github.modularbees.common.tileentities.base.TileMBModularComponent;
import com.glodblock.github.modularbees.common.tileentities.base.TileMBModularCore;
import com.glodblock.github.modularbees.util.CombCentrifugeLookup;
import com.glodblock.github.modularbees.util.GameConstants;
import com.glodblock.github.modularbees.util.GameUtil;
import com.glodblock.github.modularbees.util.TryResult;
import com.glodblock.github.modularbees.xmod.ae.expose.MEExportAction;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivelib.registry.LibItems;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.function.Consumer;

public class TileModularCentrifuge extends TileMBModularCore implements ItemHandlerHost, FluidHandlerHost, SlotListener, TankListener {

    public static final int FLUID_TANKS = 3;
    public static final int WAITING_TICKS = ProductiveBeesConfig.GENERAL.centrifugeProcessingTime.getAsInt();
    @Nullable
    private ObjectSet<BlockPos> allPos;
    @Nullable
    private ObjectSet<ChunkPos> allChunk;
    public static final Set<Item> ACCEPT_UPGRADES = Set.of(
            LibItems.UPGRADE_TIME.get(), LibItems.UPGRADE_TIME_2.get(), LibItems.UPGRADE_PRODUCTIVITY.get(),
            LibItems.UPGRADE_PRODUCTIVITY_2.get(), LibItems.UPGRADE_PRODUCTIVITY_3.get(), LibItems.UPGRADE_PRODUCTIVITY_4.get(),
            LibItems.UPGRADE_STABILITY.get()
    );
    protected final MBItemInventory upgrade = new MBItemInventory(this, 4, s -> ACCEPT_UPGRADES.contains(s.getItem())).setSlotLimit(1);
    protected final MBItemInventory inputs = new MBItemInventory(this, 3, this::validInput).inputOnly();
    protected final MBItemInventory outputs = new MBItemInventory(this, 9).outputOnly();
    private final ResourceHandler<@NotNull ItemResource> exposed = new CombinedResourceHandler<>(this.outputs, this.inputs);
    private final MBFluidInventory tanks;
    private float process = 0;
    private float tickSpeed = 1;
    private final List<ItemStack> sending = new ArrayList<>();
    private final List<FluidStack> filling = new ArrayList<>();
    private boolean stuck = false;
    private int para = 1;
    private float chanceBoost = 0;
    private CombinedInputInventory combinedInputs;
    private TileCentrifugeHeater heater;

    public TileModularCentrifuge(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.tanks = new MBFluidInventory(this, 3, 64 * GameConstants.BUCKET).outputOnly();
    }

    private CombinedInputInventory getCombinedInputs() {
        if (this.combinedInputs == null) {
            List<MBItemInventory> inv = new ArrayList<>();
            inv.add(this.inputs);
            for (var input : this.getComponents(TileCentrifugeImport.class)) {
                inv.add(input.getItemInventory());
            }
            this.combinedInputs = new CombinedInputInventory(inv);
            this.stuck = false;
        }
        return this.combinedInputs;
    }

    @Override
    protected void logicTick(@NotNull Level world, BlockState state, List<TileMBModularComponent> components) {
        if (this.isLoaded() && !this.stuck) {
            if (!this.sending.isEmpty() || !this.filling.isEmpty()) {
                var meExports = this.getComponents(MEExportAction.class);
                for (var component : meExports) {
                    if (!this.sending.isEmpty()) {
                        component.sendToMENetwork(this.sending);
                        this.sending.removeIf(ItemStack::isEmpty);
                    }
                    if (!this.filling.isEmpty()) {
                        component.sendToMENetworkFluid(this.filling);
                        this.filling.removeIf(FluidStack::isEmpty);
                    }
                }
                for (var stack : this.sending) {
                    if (stack.isEmpty()) {
                        continue;
                    }
                    try (var trans = Transaction.openRoot()) {
                        var added = this.outputs.forceInsert(ItemResource.of(stack), stack.getCount(), trans);
                        stack.shrink(added);
                        trans.commit();
                    }
                }
                this.sending.removeIf(ItemStack::isEmpty);
                if (!this.sending.isEmpty()) {
                    this.stuck = true;
                    this.process = 0;
                }
                for (var stack : this.filling) {
                    if (stack.isEmpty()) {
                        continue;
                    }
                    try (var trans = Transaction.openRoot()) {
                        var filled = this.tanks.forceInsert(FluidResource.of(stack), stack.getAmount(), trans);
                        stack.shrink(filled);
                        trans.commit();
                    }
                }
                this.filling.removeIf(FluidStack::isEmpty);
                if (!this.filling.isEmpty()) {
                    this.stuck = true;
                    this.process = 0;
                }
                this.markDirty();
            }
            if (this.emptyInput()) {
                if (this.sending.isEmpty() && this.filling.isEmpty()) {
                    this.stuck = true;
                }
                this.process = 0;
                return;
            }
            if (this.sending.isEmpty() && this.filling.isEmpty()) {
                if (!this.stuck) {
                    float overclock = 1;
                    if (this.heater != null) {
                        if (this.heater.check(this.para)) {
                            overclock *= 3;
                        } else {
                            return;
                        }
                    }
                    for (var component : components) {
                        if (component instanceof TileCentrifugeOverclocker overclocker) {
                            overclock += overclocker.getBoostAndConsume(1);
                        }
                    }
                    overclock = Math.max(1, overclock);
                    this.addTick(overclock);
                    if (this.process >= WAITING_TICKS) {
                        this.process = 0;
                        int left = this.para;
                        float boost = 1;
                        for (var component : components) {
                            if (component instanceof TileCentrifugeGearbox gearbox) {
                                boost += gearbox.getBoostAndConsume();
                            }
                        }
                        boost = Math.max(1, boost);
                        left = Math.round(left * boost);
                        for (int x = 0; x < this.getCombinedInputs().size(); x ++) {
                            if (left >= 0) {
                                var combType = this.getCombinedInputs().getResource(x);
                                var combAmount = this.getCombinedInputs().getAmountAsInt(x);
                                int used = Math.min(left, combAmount);
                                if (CombCentrifugeLookup.query(this.sending::add, this.filling::add, combType, world, used, this.chanceBoost, this.heater != null)) {
                                    try (var trans = Transaction.openRoot()) {
                                        var rmv = this.getCombinedInputs().forceExtract(x, combType, used, trans);
                                        if (rmv == used) {
                                            left -= used;
                                            this.markDirty();
                                            trans.commit();
                                        }
                                    }
                                }
                            }
                        }
                        if (left == this.para) {
                            this.stuck = true;
                        }
                    }
                }
            }
        }
    }

    public List<ItemStack> getSending() {
        return this.sending;
    }

    public List<FluidStack> getFilling() {
        return this.filling;
    }

    public boolean validInput(ItemResource stack) {
        return CombCentrifugeLookup.validInput(stack, this.level);
    }

    public void addTick(float overclock) {
        this.process += this.tickSpeed * overclock;
    }

    public double getProcess() {
        return this.process;
    }

    public void setProcess(double value) {
        this.process = (float) value;
    }

    public void setTankFluid(int slot, FluidStack stack) {
        this.tanks.set(slot, FluidResource.of(stack), stack.getAmount());
    }

    @Override
    public MBItemInventory getHandlerByName(String name) {
        return switch (name) {
            case "outputs" -> this.outputs;
            case "inputs" -> this.inputs;
            case "upgrade" -> this.upgrade;
            default -> null;
        };
    }

    @NotNull
    public Collection<BlockPos> getPoses() {
        if (this.allPos == null) {
            this.allPos = new ObjectOpenHashSet<>();
            var face = MBSingletons.MODULAR_CENTRIFUGE_CORE.get().getFacing(this.getBlockState());
            if (face == null) {
                return this.allPos;
            }
            var corePos = this.getBlockPos();
            for (int y = corePos.getY() - 1; y <= corePos.getY() + 1; y ++) {
                int upperZ, lowerZ, upperX, lowerX;
                if (face.getAxis() == Direction.Axis.X) {
                    upperZ = corePos.getZ() + 1;
                    lowerZ = corePos.getZ() - 1;
                    upperX = corePos.getX() + (-face.getStepX() + 1);
                    lowerX = corePos.getX() - (face.getStepX() + 1);
                } else if (face.getAxis() == Direction.Axis.Z) {
                    upperZ = corePos.getZ() + (-face.getStepZ() + 1);
                    lowerZ = corePos.getZ() - (face.getStepZ() + 1);
                    upperX = corePos.getX() + 1;
                    lowerX = corePos.getX() - 1;
                } else {
                    return this.allPos;
                }
                if (y == corePos.getY() - 1) {
                    this.allPos.add(new BlockPos(upperX, y, upperZ));
                    this.allPos.add(new BlockPos(upperX, y, lowerZ));
                    this.allPos.add(new BlockPos(lowerX, y, upperZ));
                    this.allPos.add(new BlockPos(lowerX, y, lowerZ));
                } else {
                    for (int x = lowerX; x <= upperX; x ++) {
                        for (int z = lowerZ; z <= upperZ; z ++) {
                            this.allPos.add(new BlockPos(x, y, z));
                        }
                    }
                }
            }
        }
        return this.allPos;
    }

    @NotNull
    public Collection<ChunkPos> getChunks() {
        if (this.allChunk == null) {
            this.allChunk = new ObjectOpenHashSet<>();
            for (var pos : this.getPoses()) {
                this.allChunk.add(ChunkPos.containing(pos));
            }
        }
        return this.allChunk;
    }

    @Override
    public void onStateChange() {
        this.allChunk = null;
        this.allPos = null;
        this.combinedInputs = null;
        super.onStateChange();
    }

    @Override
    public void formStructure() {
        super.formStructure();
        if (this.isFormed()) {
            this.combinedInputs = null;
            this.stuck = false;
        }
    }

    @Override
    public void saveTag(ValueOutput data) {
        super.saveTag(data);
        this.outputs.serialize(data.child("outputs"));
        this.inputs.serialize(data.child("inputs"));
        this.upgrade.serialize(data.child("upgrade"));
        this.tanks.serialize(data.child("tanks"));
        data.putFloat("process", this.process);
        GameUtil.saveItemList(this.sending, data, "sending");
        GameUtil.saveFluidList(this.filling, data, "filling");
    }

    @Override
    public void loadTag(ValueInput data) {
        super.loadTag(data);
        data.child("outputs").ifPresent(this.outputs::deserialize);
        data.child("inputs").ifPresent(this.inputs::deserialize);
        data.child("upgrade").ifPresent(this.upgrade::deserialize);
        data.child("tanks").ifPresent(this.tanks::deserialize);
        this.process = data.getFloatOr("process", 0);
        GameUtil.loadItemList(this.sending, data, "sending");
        GameUtil.loadFluidList(this.filling, data, "filling");
    }

    @Override
    public boolean isStructurePos(BlockPos pos) {
        // 3x2x3 cube with 4 corners
        var face = MBSingletons.MODULAR_CENTRIFUGE_CORE.get().getFacing(this.getBlockState());
        if (face == null) {
            return false;
        }
        return this.getPoses().contains(pos);
    }

    @Override
    public boolean isStructurePos(ChunkPos pos) {
        var face = MBSingletons.MODULAR_CENTRIFUGE_CORE.get().getFacing(this.getBlockState());
        if (face == null) {
            return false;
        }
        return this.getChunks().contains(pos);
    }

    @Override
    protected TryResult buildStructure(Consumer<TileMBModularComponent> collector, Level world) {
        var face = MBSingletons.MODULAR_CENTRIFUGE_CORE.get().getFacing(this.getBlockState());
        this.heater = null;
        if (face == null) {
            return TryResult.fail("modularbees.chat.data_corrupted");
        }
        var poses = this.getPoses();
        if (poses.isEmpty()) {
            return TryResult.fail("modularbees.chat.data_corrupted");
        }
        for (var pos : poses) {
            if (pos.equals(this.getBlockPos())) {
                continue;
            }
            var te = world.getBlockEntity(pos);
            if (te instanceof TileMBModularComponent centrifugePart && !centrifugePart.isActive()) {
                var block = te.getBlockState().getBlock();
                if (!(block instanceof Centrifuge)) {
                    return TryResult.fail("modularbees.chat.block_invalid", pos, pos.getX(), pos.getY(), pos.getZ());
                }
                if (te instanceof TileCentrifugeHeater h) {
                    if (this.heater == null) {
                        this.heater = h;
                    } else {
                        // Multip Heaters
                        return TryResult.fail("modularbees.chat.multiple_heater");
                    }
                }
                collector.accept(centrifugePart);
            } else {
                return TryResult.fail("modularbees.chat.block_invalid", pos, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return TryResult.SUCCESS;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.updateUpgrade();
    }

    private boolean emptyInput() {
        for (var x = 0; x < this.getCombinedInputs().size(); x ++) {
            var amount = this.getCombinedInputs().getAmountAsInt(x);
            if (amount <= 0) {
                continue;
            }
            if (this.heater != null) {
                return false;
            } else {
                var type = this.getCombinedInputs().getResource(x);
                if (!type.is(ModTags.Common.STORAGE_BLOCK_HONEYCOMBS)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onChange(ResourceHandler<@NotNull ItemResource> inv, int slot) {
        if (inv == this.upgrade) {
            this.updateUpgrade();
        } else if (inv == this.outputs ||  inv == this.inputs) {
            this.stuck = false;
        }
    }

    @Override
    public void onChange(ResourceHandler<@NotNull FluidResource> tank) {
        this.stuck = false;
    }

    public void unblock() {
        this.stuck = false;
    }

    @Override
    public MBFluidInventory getFluidInventory() {
        return this.tanks;
    }

    @Override
    public ResourceHandler<@NotNull ItemResource> getItemInventory() {
        return this.exposed;
    }

    @Override
    public void addInventoryDrops(Level level, @NotNull BlockPos pos, List<ItemStack> drops) {
        drops.addAll(this.outputs.toList());
        drops.addAll(this.inputs.toList());
        drops.addAll(this.upgrade.toList());
    }

    private void updateUpgrade() {
        double timeDiscount = (this.upgrade.countStack(LibItems.UPGRADE_TIME.get()) + 2 * this.upgrade.countStack(LibItems.UPGRADE_TIME_2.get())) * ProductiveBeesConfig.UPGRADES.timeBonus.get();
        if (timeDiscount >= 1) {
            this.tickSpeed = WAITING_TICKS * 2;
        } else {
            this.tickSpeed = (float) (1 / (1 - timeDiscount));
        }
        this.para = 0;
        this.para += this.upgrade.countStack(LibItems.UPGRADE_PRODUCTIVITY.get()) * 4;
        this.para += this.upgrade.countStack(LibItems.UPGRADE_PRODUCTIVITY_2.get()) * 8;
        this.para += this.upgrade.countStack(LibItems.UPGRADE_PRODUCTIVITY_3.get()) * 16;
        this.para += this.upgrade.countStack(LibItems.UPGRADE_PRODUCTIVITY_4.get()) * 32;
        if (this.para <= 0) {
            this.para = 1;
        }
        this.chanceBoost = 0;
        this.chanceBoost += (float) (this.upgrade.countStack(LibItems.UPGRADE_STABILITY.get()) * ProductiveBeesConfig.UPGRADES.stabilityChanceIncrease.get());
    }

    private static class CombinedInputInventory extends CombinedResourceHandler<@NotNull ItemResource> {

        public CombinedInputInventory(SequencedCollection<MBItemInventory> handlers) {
            super(handlers);
        }

        @NotNull
        protected MBItemInventory getHandlerFromIndex(int handlerIndex) {
            return (MBItemInventory) super.getHandlerFromIndex(handlerIndex);
        }

        public int forceExtract(int slot, @NotNull ItemResource item, int amount, @NotNull TransactionContext transaction) {
            int handlerIndex = getHandlerIndex(slot);
            return this.getHandlerFromIndex(handlerIndex).forceExtract(getSlotFromIndex(slot, handlerIndex), item, amount, transaction);
        }

    }

}
