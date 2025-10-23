package com.glodblock.github.modularbees.common.tileentities.hive;

import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.common.MBConfig;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.blocks.hive.Hive;
import com.glodblock.github.modularbees.common.caps.FluidHandlerHost;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.inventory.IO;
import com.glodblock.github.modularbees.common.inventory.MBFluidInventory;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.common.inventory.SlotListener;
import com.glodblock.github.modularbees.common.tileentities.base.TileMBModularComponent;
import com.glodblock.github.modularbees.common.tileentities.base.TileMBModularCore;
import com.glodblock.github.modularbees.util.BeeTable;
import com.glodblock.github.modularbees.util.GameConstants;
import com.glodblock.github.modularbees.util.GameUtil;
import com.glodblock.github.modularbees.util.StackCacheMap;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.init.ModFluids;
import cy.jdkdigital.productivelib.registry.LibItems;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class TileModularBeehive extends TileMBModularCore implements ItemHandlerHost, FluidHandlerHost, SlotListener {

    private static final int WAITING_TICKS = ProductiveBeesConfig.GENERAL.timeInHive.getAsInt();
    @Nullable
    private ObjectSet<BlockPos> allPos;
    @Nullable
    private ObjectSet<ChunkPos> allChunk;
    public static final Set<Item> ACCEPT_UPGRADES = Set.of(
            LibItems.UPGRADE_TIME.get(), LibItems.UPGRADE_BLOCK.get(), LibItems.UPGRADE_PRODUCTIVITY.get(),
            LibItems.UPGRADE_PRODUCTIVITY_2.get(), LibItems.UPGRADE_PRODUCTIVITY_3.get(), LibItems.UPGRADE_PRODUCTIVITY_4.get()
    );
    protected final MBItemInventory outputs = new MBItemInventory(this, 18).outputOnly();
    protected final MBItemInventory upgrade = new MBItemInventory(this, 4, s -> ACCEPT_UPGRADES.contains(s.getItem())).setSlotLimit(1);
    protected final MBItemInventory bottle = new MBItemInventory(this, 2, MBItemInventory.ItemFilter.of(Items.GLASS_BOTTLE)).setIO(0, IO.IN).setIO(1, IO.OUT);
    protected final MBFluidInventory honey = new MBFluidInventory(this, 8 * GameConstants.BUCKET).outputOnly();
    private final IItemHandler exposed = new CombinedInvWrapper(this.outputs, this.bottle);
    private final BeeTable table = new BeeTable(this, this::lookup);
    private float process = 0;
    private float tickSpeed = 1;
    private final List<ItemStack> sending = new ArrayList<>();
    private boolean stuck = false;
    private boolean blockMode = false;
    private float upgradeMultiplier = 1;

    public TileModularBeehive(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileModularBeehive.class, TileModularBeehive::new, MBSingletons.MODULAR_BEEHIVE_CORE), pos, state);
    }

    @Override
    protected void logicTick(@NotNull Level world, BlockState state, List<TileMBModularComponent> components) {
        if (!this.notLoaded()) {
            this.fillBottle();
            // No Bees
            if (this.table.getBeeCount() <= 0) {
                return;
            }
            float overclock = 1;
            if (!this.stuck) {
                for (var component : components) {
                    if (component instanceof TileBeehiveOverclocker overclocker) {
                        overclock += overclocker.getBoostAndConsume(this.table.getBeeCount());
                    }
                }
                overclock = Math.max(1, overclock);
                this.addTick(overclock);
            }
            if (!this.sending.isEmpty() && !this.stuck) {
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
                }
                this.setChanged();
            }
            if (this.sending.isEmpty()) {
                if (this.process >= WAITING_TICKS) {
                    this.process = 0;
                    var outputs = new StackCacheMap(world.getRandom());
                    this.table.collectOutput(world, outputs::add);
                    float treaterMultiplier = 1;
                    int working = this.table.getWorkingBee();
                    TileBeehiveDragon dragonHive = null;
                    for (var component : components) {
                        if (component instanceof TileBeehiveTreater treater) {
                            treaterMultiplier += treater.getBoostAndConsume(working);
                        } else if (dragonHive == null && component instanceof TileBeehiveDragon dragon) {
                            dragonHive = dragon;
                        }
                    }
                    treaterMultiplier = Math.max(1, treaterMultiplier);
                    this.sending.addAll(outputs.getItems(this.blockMode, this.upgradeMultiplier * treaterMultiplier));
                    var honeyAmt = world.getRandom().nextInt(working / 2, working + 1) * MBConfig.HONEY_PRODUCE_BASE.get();
                    if (honeyAmt > 0) {
                        this.honey.forceFill(new FluidStack(ModFluids.HONEY.get(), honeyAmt), IFluidHandler.FluidAction.EXECUTE);
                    }
                    if (dragonHive != null && this.table.getDragonBee() > 0) {
                        dragonHive.addDragonBreath(this.table.getDragonBee(), world);
                    }
                    this.setChanged();
                }
            }
        }
    }

    public void addOutput(ItemStack stack) {
        if (!stack.isEmpty()) {
            this.sending.add(stack);
            this.setChanged();
        }
    }

    public void fillBottle() {
        if (this.honey.getFluidAmount() >= GameConstants.BOTTLE && this.bottle.getStackInSlot(0).getItem() == Items.GLASS_BOTTLE) {
            var left = this.bottle.forceInsertItem(1,  new ItemStack(Items.HONEY_BOTTLE), false);
            if (left.isEmpty()) {
                this.honey.drain(GameConstants.BOTTLE, IFluidHandler.FluidAction.EXECUTE);
                this.bottle.forceExtractItem(0, 1, false);
            }
        }
    }

    public void addTick(float overclock) {
        this.process += this.tickSpeed * overclock;
    }

    public void onFeederChange(IItemHandler inv, int slot) {
        this.table.feederUpdate(inv, slot);
    }

    public void onBeeChange() {
        this.table.clear();
        for (var alveary : this.getComponents(TileBeehiveAlveary.class)) {
            alveary.collectBees(this.table::loadBee);
        }
    }

    private TileBeehiveFeeder.FeedSlot lookup(BeehiveBlockEntity.BeeData data) {
        if (this.getLevel() != null) {
            var entity = data.toOccupant().createEntity(this.getLevel(), this.getBlockPos());
            if (entity instanceof Bee bee) {
                var feeders = new ArrayList<>(this.getComponents(TileBeehiveFeeder.class));
                Collections.shuffle(feeders);
                for (var feeder : feeders) {
                    var result = feeder.checkFlower(bee);
                    if (result.isSuccess()) {
                        return result;
                    }
                }
            }
        }
        return TileBeehiveFeeder.FeedSlot.FAIL;
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

    @NotNull
    public Collection<BlockPos> getPoses() {
        if (this.allPos == null) {
            this.allPos = new ObjectOpenHashSet<>();
            var face = MBSingletons.MODULAR_BEEHIVE_CORE.getFacing(this.getBlockState());
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
                for (int x = lowerX; x <= upperX; x ++) {
                    for (int z = lowerZ; z <= upperZ; z ++) {
                        this.allPos.add(new BlockPos(x, y, z));
                    }
                }
            }
        }
        return this.allPos;
    }

    @Override
    public void onStateChange() {
        this.allChunk = null;
        this.allPos = null;
        this.table.clear();
        super.onStateChange();
    }

    @Override
    public boolean isStructurePos(BlockPos pos) {
        // 3x3x3 cube
        var face = MBSingletons.MODULAR_BEEHIVE_CORE.getFacing(this.getBlockState());
        if (face == null) {
            return false;
        }
        return this.getPoses().contains(pos);
    }

    @Override
    public boolean isStructurePos(ChunkPos pos) {
        var face = MBSingletons.MODULAR_BEEHIVE_CORE.getFacing(this.getBlockState());
        if (face == null) {
            return false;
        }
        return this.getChunks().contains(pos);
    }

    @Override
    protected boolean buildStructure(Consumer<TileMBModularComponent> collector, Level world) {
        var face = MBSingletons.MODULAR_BEEHIVE_CORE.getFacing(this.getBlockState());
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
            // Each hive part belongs to one hive core
            if (te instanceof TileMBModularComponent hivePart && !hivePart.isActive()) {
                var block = te.getBlockState().getBlock();
                if (!(block instanceof Hive)) {
                    return false;
                }
                collector.accept(hivePart);
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void formStructure() {
        super.formStructure();
        if (this.isFormed()) {
            this.onBeeChange();
        }
    }

    @Override
    public void saveTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.saveTag(data, provider);
        data.put("outputs", this.outputs.serializeNBT(provider));
        data.put("upgrade", this.upgrade.serializeNBT(provider));
        data.put("bottle", this.bottle.serializeNBT(provider));
        var tank = new CompoundTag();
        this.honey.writeToNBT(provider, tank);
        data.put("honey", tank);
        var tag = GameUtil.saveItemList(this.sending, provider);
        if (!tag.isEmpty()) {
            data.put("sending", tag);
        }
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.loadTag(data, provider);
        this.outputs.deserializeNBT(provider, data.getCompound("outputs"));
        this.upgrade.deserializeNBT(provider, data.getCompound("upgrade"));
        this.bottle.deserializeNBT(provider, data.getCompound("bottle"));
        this.honey.readFromNBT(provider, data.getCompound("honey"));
        if (data.contains("sending")) {
            var tag = data.getList("sending", Tag.TAG_COMPOUND);
            GameUtil.loadItemList(tag, provider, this.sending);
        }
    }

    @Override
    public MBItemInventory getHandlerByName(String name) {
        return switch (name) {
            case "outputs" -> this.outputs;
            case "upgrade" -> this.upgrade;
            case "bottle" -> this.bottle;
            default -> null;
        };
    }

    public void onLoad() {
        super.onLoad();
        this.updateUpgrade();
    }

    @Override
    public void addInventoryDrops(Level level, @NotNull BlockPos pos, List<ItemStack> drops) {
        drops.addAll(this.outputs.toList());
        drops.addAll(this.upgrade.toList());
        drops.addAll(this.bottle.toList());
    }

    @Override
    public MBFluidInventory getFluidInventory() {
        return this.honey;
    }

    @Override
    public IItemHandler getItemInventory() {
        return this.exposed;
    }

    @Override
    public void onChange(IItemHandler inv, int slot) {
        if (inv == this.upgrade) {
            this.updateUpgrade();
        } else if (inv == this.outputs) {
            this.stuck = false;
        }
    }

    private void updateUpgrade() {
        this.blockMode = this.upgrade.countStack(stack ->
                stack.getItem() == LibItems.UPGRADE_BLOCK.get() ||
                stack.getItem() == LibItems.UPGRADE_PRODUCTIVITY_4.get()) > 0;
        int amt = this.upgrade.countStack(LibItems.UPGRADE_TIME.get());
        this.tickSpeed = (float) (1 / (1 - ProductiveBeesConfig.UPGRADES.timeBonus.get() * amt));
        this.upgradeMultiplier = 1;
        this.upgradeMultiplier += (float) (this.upgrade.countStack(LibItems.UPGRADE_PRODUCTIVITY.get()) * ProductiveBeesConfig.UPGRADES.productivityMultiplier.get());
        this.upgradeMultiplier += (float) (this.upgrade.countStack(LibItems.UPGRADE_PRODUCTIVITY_2.get()) * ProductiveBeesConfig.UPGRADES.productivityMultiplier2.get());
        this.upgradeMultiplier += (float) (this.upgrade.countStack(LibItems.UPGRADE_PRODUCTIVITY_3.get()) * ProductiveBeesConfig.UPGRADES.productivityMultiplier3.get());
        this.upgradeMultiplier += (float) (this.upgrade.countStack(LibItems.UPGRADE_PRODUCTIVITY_4.get()) * ProductiveBeesConfig.UPGRADES.productivityMultiplier4.get());
    }

}
