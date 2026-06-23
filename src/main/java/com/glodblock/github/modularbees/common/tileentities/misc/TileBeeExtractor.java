package com.glodblock.github.modularbees.common.tileentities.misc;

import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.common.MBConfig;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.common.tileentities.base.TileMBMachine;
import com.glodblock.github.modularbees.util.GameUtil;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.common.item.GeneBottle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class TileBeeExtractor extends TileMBMachine {

    public TileBeeExtractor(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileBeeExtractor.class, TileBeeExtractor::new, MBSingletons.BEE_EXTRACTOR), pos, state);
    }

    @Override
    protected MBItemInventory createInputs() {
        return new MBItemInventory(this, 2)
                .setFilter(BeeCage::isFilled, 0)
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
        var cage = this.inputs.getStackInSlot(0);
        if (cage.isEmpty()) {
            return STOP;
        }
        var bottle = this.inputs.getStackInSlot(1);
        if (bottle.isEmpty()) {
            return STOP;
        }
        var bee = BeeCage.getEntityFromStack(cage, this.level, true);
        if (bee == null || bee.getAge() < 0) {
            return STOP;
        }
        var gene = GeneBottle.getStack(bee);
        var empty = GameUtil.emptyCage(cage);
        if (this.outputs.forceInsertItem(0, gene, true).isEmpty() &&
                this.outputs.forceInsertItem(1, empty, true).isEmpty()) {
            return RUNNING;
        }
        return STOP;
    }

    @Override
    protected void runRecipe() {
        var cage = this.inputs.getStackInSlot(0);
        var bottle = this.inputs.getStackInSlot(1);
        var bee = BeeCage.getEntityFromStack(cage, this.level, true);
        if (bee == null || bee.getAge() < 0 || bottle.isEmpty()) {
            return;
        }
        this.inputs.forceExtractItem(0, 1, false);
        this.inputs.forceExtractItem(1, 1, false);
        var gene = GeneBottle.getStack(bee);
        var empty = GameUtil.emptyCage(cage);
        this.outputs.forceInsertItem(0, gene, false);
        this.outputs.forceInsertItem(1, empty, false);
    }

}
