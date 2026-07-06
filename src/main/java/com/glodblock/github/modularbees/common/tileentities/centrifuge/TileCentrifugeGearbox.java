package com.glodblock.github.modularbees.common.tileentities.centrifuge;

import com.glodblock.github.modularbees.common.MBConfig;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.util.MBTags;
import com.glodblock.github.modularbees.util.ServerTickTile;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TileCentrifugeGearbox extends TileCentrifugePart implements ServerTickTile, ItemHandlerHost {

    public static final int MAX_WAX = 1000;
    public static final int WAX_USAGE = MBConfig.GEARBOX_WAX.get();
    public static final float BOOST = (float) MBConfig.GEARBOX_BOOST.getAsDouble();
    private final MBItemInventory inv = new MBItemInventory(this, 1).setFilter(this::isWax).inputOnly();
    private int wax = 0;

    public TileCentrifugeGearbox(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public float getBoostAndConsume() {
        if (this.wax >= WAX_USAGE) {
            this.wax -= WAX_USAGE;
            this.markDirty();
            return BOOST;
        }
        return 0;
    }

    private boolean isWax(ItemResource stack) {
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
    public void saveTag(ValueOutput data) {
        super.saveTag(data);
        this.inv.serialize(data.child("inv"));
        data.putInt("wax", this.wax);
    }

    @Override
    public void loadTag(ValueInput data) {
        super.loadTag(data);
        data.child("inv").ifPresent(this.inv::deserialize);
        this.wax = Math.min(data.getIntOr("wax", 0), MAX_WAX);
    }

    @Override
    public MBItemInventory getItemInventory() {
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
                var stack = this.inv.getItemStack(0);
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
                this.markDirty();
            }
        }
    }

}
