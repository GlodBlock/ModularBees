package com.glodblock.github.modularbees.common.tileentities.centrifuge;

import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.common.MBConfig;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.util.MBTags;
import com.glodblock.github.modularbees.util.ServerTickTile;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TileCentrifugeGearbox extends TileCentrifugePart implements ServerTickTile, ItemHandlerHost {

    public static final int MAX_WAX = 1000;
    public static final int WAX_USAGE = MBConfig.GEARBOX_WAX.get();
    public static final float BOOST = (float) MBConfig.GEARBOX_BOOST.getAsDouble();
    private final MBItemInventory inv = new MBItemInventory(this, 1).setFilter(this::isWax).inputOnly();
    private int wax = 0;

    public TileCentrifugeGearbox(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileCentrifugeGearbox.class, TileCentrifugeGearbox::new, MBSingletons.MODULAR_CENTRIFUGE_GEARBOX), pos, state);
    }

    public float getBoostAndConsume() {
        if (this.wax >= WAX_USAGE) {
            this.wax -= WAX_USAGE;
            this.setChanged();
            return BOOST;
        }
        return 0;
    }

    private boolean isWax(ItemStack stack) {
        return stack.is(ModTags.Common.WAXES) || stack.is(MBTags.WAX_BLOCK);
    }

    public int getWax() {
        return this.wax;
    }

    public void setWax(int wax) {
        this.wax = wax;
    }

    @Override
    public void addInventoryDrops(Level level, @NotNull BlockPos pos, List<ItemStack> drops) {
        drops.addAll(this.inv.toList());
    }

    @Override
    public void saveTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.saveTag(data, provider);
        data.put("inv", this.inv.serializeNBT(provider));
        data.putInt("wax", this.wax);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.loadTag(data, provider);
        this.inv.deserializeNBT(provider, data.getCompound("inv"));
        this.wax = Math.min(data.getInt("wax"), MAX_WAX);
    }

    @Override
    public IItemHandler getItemInventory() {
        return this.inv;
    }

    @Override
    public MBItemInventory getHandlerByName(String name) {
        return this.inv;
    }

    @Override
    public void tickServer(Level world, BlockState state) {
        if (this.isActive()) {
            if (this.wax < MAX_WAX) {
                var stack = this.inv.getStackInSlot(0);
                if (stack.is(ModTags.Common.WAXES)) {
                    var use = Math.min(stack.getCount(), MAX_WAX - this.wax);
                    stack.shrink(use);
                    this.wax += use;
                } else if (stack.is(MBTags.WAX_BLOCK)) {
                    var use = Math.min(stack.getCount(), (MAX_WAX - this.wax) / 9);
                    if (use > 0) {
                        stack.shrink(use);
                        this.wax += use * 9;
                    }
                }
            }
        }
    }

}
