package com.glodblock.github.modularbees.common.tileentities.hive;

import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.util.GameUtil;
import com.glodblock.github.modularbees.util.ServerTickTile;
import com.glodblock.github.modularbees.util.TryResult;
import com.glodblock.github.modularbees.xmod.mek.CardboxWrap;
import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBee;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
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
import java.util.Objects;
import java.util.function.Consumer;

public class TileBeehiveAlveary extends TileBeehivePart implements ServerTickTile, ItemHandlerHost, CardboxWrap {

    public static int MAX_BEES = 5;
    protected final List<AlvearyBee> bees = new ArrayList<>();
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

    public void collectBees(Consumer<AlvearyBee> collector) {
        this.bees.forEach(collector);
    }

    public List<AlvearyBee> getBees() {
        return this.bees;
    }

    @Nullable
    public AlvearyBee getBee(int x) {
        if (x >= this.bees.size()) {
            return null;
        }
        return this.bees.get(x);
    }

    public void setBees(List<AlvearyBee> bees) {
        this.bees.clear();
        this.bees.addAll(bees);
        this.markDirty();
    }

    public void loadBees(List<BeehiveBlockEntity.Occupant> occupants) {
        this.bees.clear();
        occupants.stream().map(AlvearyBee::new).forEach(this.bees::add);
        this.notifyCore();
    }

    public boolean hasRoom() {
        return this.bees.size() < MAX_BEES;
    }

    public void addBee(Level world, Bee bee) {
        bee.stopRiding();
        bee.ejectPassengers();
        world.playSound(null, this.getBlockPos(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
        this.bees.add(new AlvearyBee(BeehiveBlockEntity.Occupant.of(bee)));
        bee.discard();
        this.markDirty();
    }

    @Override
    public void saveTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.saveTag(data, provider);
        data.put("bees", BeehiveBlockEntity.Occupant.LIST_CODEC
                .encodeStart(NbtOps.INSTANCE, this.bees.stream().map(AlvearyBee::toOccupant).toList())
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

    @Override
    public void tickServer(Level world, BlockState state) {
        var in = this.cageIn.getStackInSlot(0);
        if (in.isEmpty()) {
            return;
        }
        if (BeeCage.isFilled(in)) {
            if (this.hasRoom()) {
                var bee = BeeCage.getEntityFromStack(in, world, true);
                if (bee != null && !(bee instanceof SolitaryBee) && bee.getAge() >= 0) {
                    this.addBee(world, bee);
                    this.cageIn.setStackInSlot(0, GameUtil.emptyCage(in));
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
                    this.markDirty();
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

    public void relink() {
        if (this.isActive() && this.core instanceof TileModularBeehive hive) {
            var table = hive.getBeeTable();
            for (var bee : table.getData()) {
                if (bee.needLookup()) {
                    table.link(bee);
                }
            }
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

    public static class AlvearyBee {

        private static final TryResult FAIL = TryResult.fail(Component.translatable("modularbees.gui.modular_beehive_alveary.no_food").withStyle(ChatFormatting.RED));
        private final BeehiveBlockEntity.BeeData bee;
        private TryResult linkStatus = FAIL;

        public AlvearyBee(BeehiveBlockEntity.Occupant occupant) {
            this(new BeehiveBlockEntity.BeeData(occupant));
        }

        public AlvearyBee(BeehiveBlockEntity.Occupant occupant, boolean link) {
            this(new BeehiveBlockEntity.BeeData(occupant));
            if (link) {
                this.linkStatus = TryResult.SUCCESS;
            } else {
                this.linkStatus = FAIL;
            }
        }

        public AlvearyBee(BeehiveBlockEntity.BeeData bee) {
            this.bee = bee;
        }

        public BeehiveBlockEntity.Occupant toOccupant() {
            return this.bee.toOccupant();
        }

        public void updateLink(boolean success) {
            if (success) {
                this.linkStatus = TryResult.SUCCESS;
            } else {
                this.linkStatus = FAIL;
            }
        }

        public TryResult getLinkStatus() {
            return this.linkStatus;
        }

        public boolean getLink() {
            return this.linkStatus.ok();
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (obj.getClass() ==  AlvearyBee.class) {
                AlvearyBee other = (AlvearyBee) obj;
                return Objects.equals(this.bee, other.bee);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.bee);
        }

        @Override
        public String toString() {
            return "AlvearyBee[" + "bee=" + this.bee + ",status=" + this.linkStatus + ']';
        }

    }

}
