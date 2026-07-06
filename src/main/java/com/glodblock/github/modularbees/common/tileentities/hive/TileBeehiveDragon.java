package com.glodblock.github.modularbees.common.tileentities.hive;

import com.glodblock.github.modularbees.common.MBConfig;
import com.glodblock.github.modularbees.common.caps.FluidHandlerHost;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.fluids.FluidDragonBreath;
import com.glodblock.github.modularbees.common.inventory.IO;
import com.glodblock.github.modularbees.common.inventory.MBFluidInventory;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.util.GameConstants;
import com.glodblock.github.modularbees.util.ServerTickTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class TileBeehiveDragon extends TileBeehivePart implements ItemHandlerHost, FluidHandlerHost, ServerTickTile {

    private final MBFluidInventory tank = new MBFluidInventory(this, 16 * GameConstants.BUCKET).outputOnly();
    private final MBItemInventory bottle = new MBItemInventory(this, 2, MBItemInventory.ItemFilter.of(Items.GLASS_BOTTLE)).setIO(0, IO.IN).setIO(1, IO.OUT);

    public TileBeehiveDragon(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void addDragonBreath(int bees, Level world) {
        var amount = world.getRandom().nextInt(bees / 2, bees + 1) * MBConfig.DRAGON_BREATH_PRODUCE_BASE.get();
        if (amount > 0) {
            try (var trans = Transaction.openRoot()) {
                this.tank.forceInsert(FluidResource.of(FluidDragonBreath.getFluid()), amount, trans);
                trans.commit();
            }
        }
    }

    @Override
    public void saveTag(ValueOutput data) {
        super.saveTag(data);
        this.tank.serialize(data.child("tank"));
    }

    @Override
    public void loadTag(ValueInput data) {
        super.loadTag(data);
        data.child("tank").ifPresent(this.tank::deserialize);
    }

    @Override
    public MBFluidInventory getFluidInventory() {
        return this.tank;
    }

    @Override
    public MBItemInventory getItemInventory() {
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
        if (this.tank.getAmountAsInt(0) >= GameConstants.BOTTLE && this.bottle.getItemStack(0).getItem() == Items.GLASS_BOTTLE) {
            try (var trans = Transaction.openRoot()) {
                var added = this.bottle.forceInsert(1,  ItemResource.of(Items.DRAGON_BREATH), 1, trans);
                if (added == 1) {
                    var rmv = this.tank.forceExtract(FluidResource.of(FluidDragonBreath.getFluid()), GameConstants.BOTTLE, trans);
                    if (rmv == GameConstants.BOTTLE) {
                        var bot = this.bottle.forceExtract(0, ItemResource.of(Items.GLASS_BOTTLE), 1, trans);
                        if (bot == 1) {
                            trans.commit();
                        }
                    }
                }
            }
        }
    }

}
