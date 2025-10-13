package com.glodblock.github.modularbees.common.tileentities.hive;

import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.common.inventory.SlotListener;
import com.glodblock.github.modularbees.common.recipe.TreaterRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TileBeehiveTreater extends TileBeehivePart implements ItemHandlerHost, SlotListener {

    private final MBItemInventory foods = new MBItemInventory(this, 6).inputOnly();
    private TreaterRecipe cache = null;
    private boolean stuck = false;

    public TileBeehiveTreater(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileBeehiveTreater.class, TileBeehiveTreater::new, MBSingletons.MODULAR_TREATER), pos, state);
    }

    public float getBoostAndConsume(int bees) {
        if (bees <= 0 || !(this.core instanceof TileModularBeehive beehive)) {
            return 0;
        }
        var find = this.findRecipe();
        if (find != null) {
            var want = bees;
            for (var x = 0; x < this.foods.getSlots(); x ++) {
                var stack = this.foods.getStackInSlot(x);
                if (find.isValidInput(stack)) {
                    var extracted = this.foods.forceExtractItem(x, want, false);
                    int amt = extracted.getCount();
                    if (amt <= 0) {
                        continue;
                    }
                    if (!find.output().isEmpty()) {
                        beehive.addOutput(find.output().copyWithCount(amt));
                    }
                    want -= amt;
                    if (want <= 0) {
                        break;
                    }
                }
            }
            if (want == 0) {
                return find.boost();
            }
            return find.boost() * (bees - want) / bees;
        }
        return 0;
    }

    @Override
    public MBItemInventory getHandlerByName(String name) {
        return this.foods;
    }

    @Override
    public IItemHandler getItemInventory() {
        return this.foods;
    }

    @Override
    public void saveTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.saveTag(data, provider);
        data.put("foods", this.foods.serializeNBT(provider));
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.loadTag(data, provider);
        this.foods.deserializeNBT(provider, data.getCompound("foods"));
    }

    @Override
    public void addInventoryDrops(Level level, @NotNull BlockPos pos, List<ItemStack> drops) {
        drops.addAll(this.foods.toList());
    }

    @Nullable
    private TreaterRecipe findRecipe() {
        if (this.stuck || this.level == null) {
            return null;
        }
        if (this.cache != null) {
            if (this.testRecipe(this.cache)) {
                return this.cache;
            }
        }
        this.cache = null;
        for (var recipe : this.level.getRecipeManager().byType(TreaterRecipe.TYPE)) {
            if (this.foods.countStack(recipe.value()::isValidInput) > 0) {
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
        return this.foods.countStack(recipe::isValidInput) > 0;
    }

    @Override
    public void onChange(IItemHandler inv, int slot) {
        if (inv == this.foods) {
            this.stuck = false;
        }
    }

}
