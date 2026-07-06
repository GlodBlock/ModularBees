package com.glodblock.github.modularbees.common.tileentities.hive;

import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.common.inventory.SlotListener;
import com.glodblock.github.modularbees.common.recipe.TreaterRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TileBeehiveTreater extends TileBeehivePart implements ItemHandlerHost, SlotListener {

    private final MBItemInventory foods = new MBItemInventory(this, 6).inputOnly();
    private TreaterRecipe cache = null;
    private boolean stuck = false;

    public TileBeehiveTreater(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public float getBoostAndConsume(int bees) {
        if (bees <= 0 || !(this.core instanceof TileModularBeehive beehive)) {
            return 0;
        }
        var find = this.findRecipe();
        if (find != null) {
            var want = bees;
            for (var x = 0; x < this.foods.size(); x ++) {
                var stack = this.foods.getItemStack(x);
                if (find.isValidInput(stack)) {
                    try (var trans = Transaction.openRoot()) {
                        var extracted = this.foods.forceExtract(x, ItemResource.of(stack), want, trans);
                        if (extracted <= 0) {
                            continue;
                        }
                        if (find.output().isPresent()) {
                            beehive.addOutput(find.output().get().withCount(extracted).create());
                        }
                        want -= extracted;
                        trans.commit();
                    }
                }
                if (want <= 0) {
                    break;
                }
            }
            if (want == 0) {
                return Math.max(find.boost() - 1, 0);
            }
            return Math.max((find.boost() - 1) * (bees - want) / bees , 0);
        }
        return 0;
    }

    @Override
    public MBItemInventory getHandlerByName(String name) {
        return this.foods;
    }

    @Override
    public MBItemInventory getItemInventory() {
        return this.foods;
    }

    @Override
    public void saveTag(ValueOutput data) {
        super.saveTag(data);
        this.foods.serialize(data.child("foods"));
    }

    @Override
    public void loadTag(ValueInput data) {
        super.loadTag(data);
        data.child("foods").ifPresent(this.foods::deserialize);
    }

    @Override
    public void addInventoryDrops(Level level, @NotNull BlockPos pos, List<ItemStack> drops) {
        drops.addAll(this.foods.toList());
    }

    @Nullable
    private TreaterRecipe findRecipe() {
        if (this.stuck || !(this.level instanceof ServerLevel serverLevel)) {
            return null;
        }
        if (this.cache != null) {
            if (this.testRecipe(this.cache)) {
                return this.cache;
            }
        }
        this.cache = null;
        for (var recipe : serverLevel.recipeAccess().recipeMap().byType(TreaterRecipe.TYPE)) {
            if (this.foods.contains(recipe.value()::isValidInput)) {
                this.cache = recipe.value();
                break;
            }
        }
        if (this.cache == null) {
            this.stuck = true;
        }
        return this.cache;
    }

    private boolean testRecipe(@NotNull TreaterRecipe recipe) {
        return this.foods.contains(recipe::isValidInput);
    }

    @Override
    public void onChange(ResourceHandler<@NotNull ItemResource> inv, int slot) {
        if (inv == this.foods) {
            this.stuck = false;
        }
    }

}
