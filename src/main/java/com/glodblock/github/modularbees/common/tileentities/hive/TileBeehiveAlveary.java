package com.glodblock.github.modularbees.common.tileentities.hive;

import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.util.ServerTickTile;
import com.glodblock.github.modularbees.xmod.mek.CardboxWrap;
import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBee;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TileBeehiveAlveary extends TileBeehivePart implements ServerTickTile, ItemHandlerHost, CardboxWrap {

    public static int MAX_BEES = 5;
    protected final List<BeehiveBlockEntity.BeeData> bees = new ArrayList<>();
    protected final MBItemInventory cageIn = new MBItemInventory(this, 1, BeeCage::isFilled).setSlotLimit(1);
    protected final MBItemInventory cageOut = new MBItemInventory(this, 1, this::isEmptyCage).setSlotLimit(1);
    private final IItemHandler exposed = new CombinedInvWrapper(this.cageIn, this.cageOut);
    private boolean wrap = false;

    public TileBeehiveAlveary(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileBeehiveAlveary.class, TileBeehiveAlveary::new, MBSingletons.MODULAR_ALVEARY), pos, state);
    }

    public boolean isEmptyCage(ItemStack stack) {
        return stack.getItem() instanceof BeeCage && !BeeCage.isFilled(stack);
    }

    public void collectBees(Consumer<BeehiveBlockEntity.BeeData> collector) {
        this.bees.forEach(collector);
    }

    public List<BeehiveBlockEntity.BeeData> getBees() {
        return this.bees;
    }

    @Nullable
    public BeehiveBlockEntity.BeeData getBee(int x) {
        if (x >= this.bees.size()) {
            return null;
        }
        return this.bees.get(x);
    }

    public void setBees(List<BeehiveBlockEntity.BeeData> bees) {
        this.bees.clear();
        this.bees.addAll(bees);
        this.setChanged();
    }

    public void loadBees(List<BeehiveBlockEntity.Occupant> occupants) {
        this.bees.clear();
        occupants.stream().map(BeehiveBlockEntity.BeeData::new).forEach(this.bees::add);
        this.notifyCore();
    }

    public boolean hasRoom() {
        return this.bees.size() < MAX_BEES;
    }

    public void addBee(Level world, Bee bee) {
        bee.stopRiding();
        bee.ejectPassengers();
        world.playSound(null, this.getBlockPos(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
        this.bees.add(new BeehiveBlockEntity.BeeData(BeehiveBlockEntity.Occupant.of(bee)));
        bee.discard();
        this.setChanged();
    }

    @Override
    public void saveTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.saveTag(data, provider);
        data.put("bees", BeehiveBlockEntity.Occupant.LIST_CODEC
                .encodeStart(NbtOps.INSTANCE, this.bees.stream().map(BeehiveBlockEntity.BeeData::toOccupant).toList())
                .getOrThrow());
        data.put("cageIn", this.cageIn.serializeNBT(provider));
        data.put("cageOut", this.cageOut.serializeNBT(provider));
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.loadTag(data, provider);
        BeehiveBlockEntity.Occupant.LIST_CODEC
                .parse(NbtOps.INSTANCE, data.get("bees"))
                .resultOrPartial(bee -> ModularBees.LOGGER.error("Failed to parse bees: '{}'", bee))
                .ifPresent(this::loadBees);
        this.cageIn.deserializeNBT(provider, data.getCompound("cageIn"));
        this.cageOut.deserializeNBT(provider, data.getCompound("cageOut"));
    }

    @Override
    public MBItemInventory getHandlerByName(String name) {
        return switch (name) {
            case "in" -> this.cageIn;
            case "out" -> this.cageOut;
            default -> null;
        };
    }

    public ItemStack emptyCage(ItemStack cage) {
        if (cage.getItem() == ModItems.STURDY_BEE_CAGE.get()) {
            return new ItemStack(ModItems.STURDY_BEE_CAGE);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void tickServer(Level world, BlockState state) {
        var in = this.cageIn.getStackInSlot(0);
        if (BeeCage.isFilled(in)) {
            if (this.hasRoom()) {
                var bee = BeeCage.getEntityFromStack(in, world, true);
                if (bee != null && !(bee instanceof SolitaryBee) && bee.getAge() >= 0) {
                    this.addBee(world, bee);
                    this.cageIn.setStackInSlot(0, this.emptyCage(in));
                    this.notifyCore();
                }
            }
        }
        var out = this.cageOut.getStackInSlot(0);
        if (this.isEmptyCage(out)) {
            if (!this.bees.isEmpty()) {
                var entity = this.bees.getLast().toOccupant().createEntity(world, this.getBlockPos());
                if (entity instanceof Bee bee) {
                    bee.setHivePos(this.getBlockPos());
                    BeeCage.captureEntity(bee, out);
                    this.bees.removeLast();
                    this.cageOut.setStackInSlot(0, out);
                    this.setChanged();
                    this.notifyCore();
                }
            }
        }
    }

    public void notifyCore() {
        if (this.isActive() && this.core instanceof TileModularBeehive hive) {
            hive.onBeeChange();
        }
    }

    @Override
    public IItemHandler getItemInventory() {
        return this.exposed;
    }

    @Override
    public void addInventoryDrops(Level world, @NotNull BlockPos pos, List<ItemStack> drops) {
        if (this.wrap) {
            return;
        }
        drops.addAll(this.cageIn.toList());
        drops.addAll(this.cageOut.toList());
        var releasePos = this.getBlockPos();
        for (var dir : Direction.values()) {
            var checkPos = pos.relative(dir);
            if (world.getBlockState(checkPos).getCollisionShape(world, checkPos).isEmpty()) {
                releasePos = checkPos;
                break;
            }
        }
        for (var data : this.bees) {
            var entity = data.toOccupant().createEntity(world, releasePos);
            if (entity != null) {
                entity.setPos(releasePos.getCenter());
                world.addFreshEntity(entity);
            }
        }
    }

    @Override
    public void onWrap() {
        this.wrap = true;
    }

}
