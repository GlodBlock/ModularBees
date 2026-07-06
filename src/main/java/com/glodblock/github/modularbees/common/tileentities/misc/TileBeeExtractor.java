package com.glodblock.github.modularbees.common.tileentities.misc;

import com.glodblock.github.modularbees.common.MBConfig;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.common.tileentities.base.TileMBMachine;
import com.glodblock.github.modularbees.util.GameUtil;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.common.item.GeneBottle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.Set;

public class TileBeeExtractor extends TileMBMachine {

    public TileBeeExtractor(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected MBItemInventory createInputs() {
        return new MBItemInventory(this, 2)
                .setFilter(MBItemInventory.ItemFilter.of(BeeCage::isFilled), 0)
                .setFilter(MBItemInventory.ItemFilter.of(Items.GLASS_BOTTLE), 1)
                .inputOnly();
    }

    @Override
    protected MBItemInventory createOutputs() {
        return new MBItemInventory(this, 2).outputOnly();
    }

    @Override
    protected Set<Item> validUpgrades() {
        return TIME_UPGRADES;
    }

    @Override
    protected int getMaxProcessTime() {
        return MBConfig.BEE_EXTRACTOR_TIME.get();
    }

    @Override
    protected int getPowerUse() {
        return MBConfig.BEE_EXTRACTOR_POWER_USAGE.get();
    }

    @Override
    protected int checkStatus() {
        var cage = this.inputs.getItemStack(0);
        if (cage.isEmpty()) {
            return STOP;
        }
        var bottle = this.inputs.getItemStack(1);
        if (bottle.isEmpty()) {
            return STOP;
        }
        var bee = BeeCage.getEntityFromStack(cage, this.level, true);
        if (bee == null || bee.getAge() < 0) {
            return STOP;
        }
        var gene = GeneBottle.getStack(bee);
        var empty = GameUtil.emptyCage(cage);
        try (var trans = Transaction.openRoot()) {
            if (this.outputs.forceInsert(0, ItemResource.of(gene), 1, trans) == 1) {
                boolean success = false;
                if (!empty.isEmpty()) {
                    if (this.outputs.forceInsert(1, ItemResource.of(empty), 1, trans) == 1) {
                        success = true;
                    }
                } else {
                    success = true;
                }
                if (success) {
                    return RUNNING;
                }
            }
        }
        return STOP;
    }

    @Override
    protected void runRecipe() {
        var cage = this.inputs.getItemStack(0);
        var bottle = this.inputs.getItemStack(1);
        var bee = BeeCage.getEntityFromStack(cage, this.level, true);
        if (bee == null || bee.getAge() < 0 || bottle.isEmpty()) {
            return;
        }
        try (var trans = Transaction.openRoot()) {
            if (this.inputs.forceExtract(0, this.inputs.getResource(0), 1, trans) == 1 &&
                    this.inputs.forceExtract(1, this.inputs.getResource(1), 1, trans) == 1) {
                var gene = GeneBottle.getStack(bee);
                var empty = GameUtil.emptyCage(cage);
                if (this.outputs.forceInsert(0, ItemResource.of(gene), 1, trans) == 1) {
                    boolean success = false;
                    if (!empty.isEmpty()) {
                        if (this.outputs.forceInsert(1, ItemResource.of(empty), 1, trans) == 1) {
                            success = true;
                        }
                    } else {
                        success = true;
                    }
                    if (success) {
                        trans.commit();
                    }
                }
            }
        }
    }

}
