package com.glodblock.github.modularbees.common.tileentities.hive;

import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.common.MBConfig;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.caps.FluidHandlerHost;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.fluids.FluidDragonBreath;
import com.glodblock.github.modularbees.common.inventory.IO;
import com.glodblock.github.modularbees.common.inventory.MBFluidInventory;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.util.GameConstants;
import com.glodblock.github.modularbees.util.ServerTickTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class TileBeehiveDragon extends TileBeehivePart implements ItemHandlerHost, FluidHandlerHost, ServerTickTile {

    private final MBFluidInventory tank = new MBFluidInventory(this, 16 * GameConstants.BUCKET).outputOnly();
    private final MBItemInventory bottle = new MBItemInventory(this, 2, MBItemInventory.ItemFilter.of(Items.GLASS_BOTTLE)).setIO(1, IO.OUT);

    public TileBeehiveDragon(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileBeehiveDragon.class, TileBeehiveDragon::new, MBSingletons.MODULAR_DRAGON_HIVE), pos, state);
    }

    public void addDragonBreath(int bees, Level world) {
        var amount = world.random.nextInt(bees / 2, bees + 1) * MBConfig.DRAGON_BREATH_PRODUCE_BASE.get();
        if (amount > 0) {
            this.tank.forceFill(new FluidStack(FluidDragonBreath.getFluid(), amount), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    public void saveTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.saveTag(data, provider);
        var tank = new CompoundTag();
        this.tank.writeToNBT(provider, tank);
        data.put("tank", tank);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.loadTag(data, provider);
        this.tank.readFromNBT(provider, data.getCompound("tank"));
    }

    @Override
    public MBFluidInventory getFluidInventory() {
        return this.tank;
    }

    @Override
    public IItemHandler getItemInventory() {
        return this.bottle;
    }

    @Override
    public MBItemInventory getHandlerByName(String name) {
        return this.bottle;
    }

    @Override
    public void tickServer(Level world, BlockState state) {
        this.fillBottle();
    }

    private void fillBottle() {
        if (this.tank.getFluidAmount() >= GameConstants.BOTTLE && this.bottle.getStackInSlot(0).getItem() == Items.GLASS_BOTTLE) {
            var left = this.bottle.forceInsertItem(1,  new ItemStack(Items.DRAGON_BREATH), false);
            if (left.isEmpty()) {
                this.tank.drain(GameConstants.BOTTLE, IFluidHandler.FluidAction.EXECUTE);
                this.bottle.extractItem(0, 1, false);
            }
        }
    }

}
