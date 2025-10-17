package com.glodblock.github.modularbees.common.tileentities.centrifuge;

import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.blocks.centrifuge.Centrifuge;
import com.glodblock.github.modularbees.common.caps.FluidHandlerHost;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.inventory.MBFluidInventory;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.common.inventory.RandomAccessTank;
import com.glodblock.github.modularbees.common.inventory.SlotListener;
import com.glodblock.github.modularbees.common.tileentities.base.TileMBModularComponent;
import com.glodblock.github.modularbees.common.tileentities.base.TileMBModularCore;
import com.glodblock.github.modularbees.util.CombCentrifugeLookup;
import com.glodblock.github.modularbees.util.GameConstants;
import com.glodblock.github.modularbees.util.GameUtil;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivelib.registry.LibItems;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class TileModularCentrifuge extends TileMBModularCore implements ItemHandlerHost, FluidHandlerHost, SlotListener {

    public static final int FLUID_TANKS = 3;
    public static final int WAITING_TICKS = ProductiveBeesConfig.GENERAL.centrifugePoweredProcessingTime.getAsInt();
    @Nullable
    private ObjectSet<BlockPos> allPos;
    @Nullable
    private ObjectSet<ChunkPos> allChunk;
    public static final Set<Item> ACCEPT_UPGRADES = Set.of(
            LibItems.UPGRADE_TIME.get(), LibItems.UPGRADE_TIME_2.get(), LibItems.UPGRADE_PRODUCTIVITY.get(),
            LibItems.UPGRADE_PRODUCTIVITY_2.get(), LibItems.UPGRADE_PRODUCTIVITY_3.get(), LibItems.UPGRADE_PRODUCTIVITY_4.get()
    );
    protected final MBItemInventory upgrade = new MBItemInventory(this, 4, s -> ACCEPT_UPGRADES.contains(s.getItem())).setSlotLimit(1);
    protected final MBItemInventory inputs = new MBItemInventory(this, 3, this::validInput).inputOnly();
    protected final MBItemInventory outputs = new MBItemInventory(this, 9).outputOnly();
    private final IItemHandler exposed = new CombinedInvWrapper(this.outputs, this.inputs);
    private final MultiTank tanks;
    private float process = 0;
    private float tickSpeed = 1;
    private final List<ItemStack> sending = new ArrayList<>();
    private final List<FluidStack> filling = new ArrayList<>();
    private boolean stuck = false;
    private int para = 1;

    public TileModularCentrifuge(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileModularCentrifuge.class, TileModularCentrifuge::new, MBSingletons.MODULAR_CENTRIFUGE_CORE), pos, state);
        this.tanks = new MultiTank(new MBFluidInventory[] {
                new MBFluidInventory(this, 64 * GameConstants.BUCKET).outputOnly(),
                new MBFluidInventory(this, 64 * GameConstants.BUCKET).outputOnly(),
                new MBFluidInventory(this, 64 * GameConstants.BUCKET).outputOnly(),
        });
    }

    @Override
    protected void logicTick(@NotNull Level world, BlockState state, List<TileMBModularComponent> components) {
        if (!this.notLoaded() && !this.stuck) {
            if (!this.sending.isEmpty() || !this.filling.isEmpty()) {
                for (int i = 0; i < this.sending.size(); ++i) {
                    var stack = this.sending.get(i).copy();
                    for (int x = 0; x < this.outputs.getSlots(); ++x) {
                        if (stack.isEmpty()) {
                            break;
                        }
                        stack = this.outputs.forceInsertItem(x, stack, false);
                    }
                    this.sending.set(i, stack);
                }
                this.sending.removeIf(ItemStack::isEmpty);
                if (!this.sending.isEmpty()) {
                    this.stuck = true;
                    this.process = 0;
                }
                for (int i = 0; i < this.filling.size(); ++i) {
                    var stack = this.filling.get(i).copy();
                    if (stack.isEmpty()) {
                        continue;
                    }
                    var filled = this.tanks.forceFill(stack, IFluidHandler.FluidAction.EXECUTE);
                    stack.shrink(filled);
                    this.filling.set(i, stack);
                }
                this.filling.removeIf(FluidStack::isEmpty);
                if (!this.filling.isEmpty()) {
                    this.stuck = true;
                    this.process = 0;
                }
                this.setChanged();
            }
            if (this.emptyInput()) {
                if (this.sending.isEmpty() && this.filling.isEmpty()) {
                    this.stuck = true;
                }
                this.process = 0;
                return;
            }
            if (this.sending.isEmpty() && this.filling.isEmpty()) {
                float overclock = 1;
                if (!this.stuck) {
                    for (var component : components) {
                        if (component instanceof TileCentrifugeOverclocker overclocker) {
                            overclock += overclocker.getBoostAndConsume(1);
                        }
                    }
                }
                overclock = Math.max(1, overclock);
                this.addTick(overclock);
                if (this.process >= WAITING_TICKS) {
                    this.process = 0;
                    int left = this.para;
                    for (int x = 0; x < this.inputs.getSlots(); x ++) {
                        if (left >= 0) {
                            var comb = this.inputs.getStackInSlot(x);
                            int used = Math.min(left, comb.getCount());
                            if (CombCentrifugeLookup.query(this.sending::add, this.filling::add, comb, world, used)) {
                                left -= used;
                                comb.shrink(used);
                                this.inputs.setStackInSlot(x, comb);
                                this.setChanged();
                            }
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

    private boolean validInput(ItemStack stack) {
        return stack.is(ModTags.Common.HONEYCOMBS) || stack.is(ModTags.Common.STORAGE_BLOCK_HONEYCOMBS);
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
        this.tanks.tanks[slot].setFluid(stack);
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
            var face = MBSingletons.MODULAR_CENTRIFUGE_CORE.getFacing(this.getBlockState());
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
                this.allChunk.add(new ChunkPos(pos));
            }
        }
        return this.allChunk;
    }

    @Override
    public void onStateChange() {
        this.allChunk = null;
        this.allPos = null;
        super.onStateChange();
    }

    @Override
    public void saveTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.saveTag(data, provider);
        data.put("outputs", this.outputs.serializeNBT(provider));
        data.put("inputs", this.inputs.serializeNBT(provider));
        data.put("upgrade", this.upgrade.serializeNBT(provider));
        data.put("tanks", this.tanks.serializeNBT(provider));
        data.putFloat("process", this.process);
        var tag = GameUtil.saveItemList(this.sending, provider);
        if (!tag.isEmpty()) {
            data.put("sending", tag);
        }
        var tagFluid = GameUtil.saveFluidList(this.filling, provider);
        if (!tagFluid.isEmpty()) {
            data.put("filling", tagFluid);
        }
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.loadTag(data, provider);
        this.outputs.deserializeNBT(provider, data.getCompound("outputs"));
        this.inputs.deserializeNBT(provider, data.getCompound("inputs"));
        this.upgrade.deserializeNBT(provider, data.getCompound("upgrade"));
        this.tanks.deserializeNBT(provider, data.getCompound("tanks"));
        this.process = data.getFloat("process");
        if (data.contains("sending")) {
            var tag = data.getList("sending", Tag.TAG_COMPOUND);
            GameUtil.loadItemList(tag, provider, this.sending);
        }
        if (data.contains("filling")) {
            var tag = data.getList("filling", Tag.TAG_COMPOUND);
            GameUtil.loadFluidList(tag, provider, this.filling);
        }
    }

    @Override
    public boolean isStructurePos(BlockPos pos) {
        // 3x2x3 cube with 4 corners
        var face = MBSingletons.MODULAR_CENTRIFUGE_CORE.getFacing(this.getBlockState());
        if (face == null) {
            return false;
        }
        return this.getPoses().contains(pos);
    }

    @Override
    public boolean isStructurePos(ChunkPos pos) {
        var face = MBSingletons.MODULAR_CENTRIFUGE_CORE.getFacing(this.getBlockState());
        if (face == null) {
            return false;
        }
        return this.getChunks().contains(pos);
    }

    @Override
    protected boolean buildStructure(Consumer<TileMBModularComponent> collector, Level world) {
        var face = MBSingletons.MODULAR_CENTRIFUGE_CORE.getFacing(this.getBlockState());
        if (face == null) {
            return false;
        }
        var poses = this.getPoses();
        if (poses.isEmpty()) {
            return false;
        }
        for (var pos : poses) {
            if (pos.equals(this.getBlockPos())) {
                continue;
            }
            var te = world.getBlockEntity(pos);
            if (te instanceof TileMBModularComponent centrifugePart && !centrifugePart.isActive()) {
                var block = te.getBlockState().getBlock();
                if (!(block instanceof Centrifuge)) {
                    return false;
                }
                collector.accept(centrifugePart);
            } else {
                return false;
            }
        }
        return true;
    }

    public void onLoad() {
        super.onLoad();
        this.updateUpgrade();
    }

    private boolean emptyInput() {
        return !this.inputs.hasItem();
    }

    @Override
    public void onChange(IItemHandler inv, int slot) {
        if (inv == this.upgrade) {
            this.updateUpgrade();
        } else if (inv == this.outputs ||  inv == this.inputs) {
            this.stuck = false;
        }
    }

    @Override
    public RandomAccessTank getFluidInventory() {
        return this.tanks;
    }

    @Override
    public IItemHandler getItemInventory() {
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
    }

    private record MultiTank(MBFluidInventory[] tanks) implements RandomAccessTank, INBTSerializable<CompoundTag> {

        @Override
        public int getTanks() {
            return this.tanks.length;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return this.tanks[tank].getFluid();
        }

        @Override
        public int getTankCapacity(int tank) {
            return this.tanks[tank].getCapacity();
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return this.tanks[tank].isFluidValid(stack);
        }

        // Output only
        @Override
        public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
            return 0;
        }

        public int forceFill(FluidStack resource, IFluidHandler.FluidAction action) {
            if (resource.isEmpty()) {
                return 0;
            }
            int tot = resource.getAmount();
            for (var tank : this.tanks) {
                if (resource.isEmpty()) {
                    return tot;
                }
                if (tank.getFluid().is(resource.getFluid())) {
                    int filled = tank.forceFill(resource, action);
                    resource.shrink(filled);
                }
            }
            for (var tank : this.tanks) {
                if (resource.isEmpty()) {
                    return tot;
                }
                if (tank.getFluid().isEmpty()) {
                    int filled = tank.forceFill(resource, action);
                    resource.shrink(filled);
                }
            }
            return tot - resource.getAmount();
        }

        @Override
        public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
            var fluid = resource.copy();
            for (var tank : this.tanks) {
                if (resource.isEmpty()) {
                    return fluid;
                }
                var drained = tank.drain(resource, action);
                resource.shrink(drained.getAmount());
            }
            fluid.shrink(resource.getAmount());
            return fluid;
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
            for (var tank : this.tanks) {
                if (!tank.getFluid().isEmpty()) {
                    return drain(tank.getFluid().copyWithAmount(maxDrain), action);
                }
            }
            return FluidStack.EMPTY;
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            var nbt = new CompoundTag();
            for (int x = 0; x < this.tanks.length; x ++) {
                if (!this.tanks[x].getFluid().isEmpty()) {
                    var tankTag = this.tanks[x].writeToNBT(provider, new CompoundTag());
                    nbt.put("#" + x, tankTag);
                }
            }
            return nbt;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
            for (int x = 0; x < this.tanks.length; x ++) {
                if (nbt.contains("#" + x, Tag.TAG_COMPOUND)) {
                    var tankTag = nbt.getCompound("#" + x);
                    this.tanks[x].readFromNBT(provider, tankTag);
                } else {
                    this.tanks[x].setFluid(FluidStack.EMPTY);
                }
            }
        }

        @Override
        public IFluidTank getTank(int tank) {
            return this.tanks[tank];
        }

    }

}
