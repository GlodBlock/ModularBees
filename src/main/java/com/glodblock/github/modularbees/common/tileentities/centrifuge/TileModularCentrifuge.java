package com.glodblock.github.modularbees.common.tileentities.centrifuge;

import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.caps.FluidHandlerHost;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.inventory.MBFluidInventory;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.common.tileentities.base.TileMBModularComponent;
import com.glodblock.github.modularbees.common.tileentities.base.TileMBModularCore;
import com.glodblock.github.modularbees.util.GameConstants;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivelib.registry.LibItems;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class TileModularCentrifuge extends TileMBModularCore implements ItemHandlerHost, FluidHandlerHost {

    private static final int WAITING_TICKS = ProductiveBeesConfig.GENERAL.centrifugePoweredProcessingTime.getAsInt();
    @Nullable
    private ObjectSet<BlockPos> allPos;
    @Nullable
    private ObjectSet<ChunkPos> allChunk;
    public static final Set<Item> ACCEPT_UPGRADES = Set.of(
            LibItems.UPGRADE_TIME.get(), LibItems.UPGRADE_TIME_2.get(), LibItems.UPGRADE_PRODUCTIVITY.get(),
            LibItems.UPGRADE_PRODUCTIVITY_2.get(), LibItems.UPGRADE_PRODUCTIVITY_3.get(), LibItems.UPGRADE_PRODUCTIVITY_4.get()
    );
    protected final MBItemInventory upgrade = new MBItemInventory(this, 4, s -> ACCEPT_UPGRADES.contains(s.getItem())).setSlotLimit(1);
    protected final MBItemInventory inputs = new MBItemInventory(this, 3).inputOnly();
    protected final MBItemInventory outputs = new MBItemInventory(this, 9).outputOnly();
    protected final IItemHandler exposed = new CombinedInvWrapper(this.outputs, this.inputs);
    protected final IFluidHandler tanks;

    public TileModularCentrifuge(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileModularCentrifuge.class, TileModularCentrifuge::new, MBSingletons.MODULAR_CENTRIFUGE_CORE), pos, state);
        this.tanks = new MultiTank(new MBFluidInventory[] {
                new MBFluidInventory(this, 64 * GameConstants.BUCKET).outputOnly(),
                new MBFluidInventory(this, 64 * GameConstants.BUCKET).outputOnly(),
                new MBFluidInventory(this, 64 * GameConstants.BUCKET).outputOnly(),
        });
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
            if (te instanceof TileCentrifugePart centrifugePart && !centrifugePart.isActive()) {
                collector.accept(centrifugePart);
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public IFluidHandler getFluidInventory() {
        return this.tanks;
    }

    @Override
    public IItemHandler getItemInventory() {
        return null;
    }

    @Override
    protected void logicTick(@NotNull Level world, BlockState state, List<TileMBModularComponent> components) {

    }

    private record MultiTank(MBFluidInventory[] tanks) implements IFluidHandler {

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

    }

}
