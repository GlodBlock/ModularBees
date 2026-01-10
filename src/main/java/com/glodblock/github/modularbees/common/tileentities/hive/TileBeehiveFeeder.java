package com.glodblock.github.modularbees.common.tileentities.hive;

import com.glodblock.github.glodium.util.GlodUtil;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.caps.ItemHandlerHost;
import com.glodblock.github.modularbees.common.inventory.MBItemInventory;
import com.glodblock.github.modularbees.common.inventory.SlotListener;
import com.glodblock.github.modularbees.util.GameUtil;
import cy.jdkdigital.productivebees.common.block.Amber;
import cy.jdkdigital.productivebees.common.block.entity.AmberBlockEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.entity.bee.hive.RancherBee;
import cy.jdkdigital.productivebees.init.ModTags;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class TileBeehiveFeeder extends TileBeehivePart implements ItemHandlerHost, SlotListener {

    protected final MBItemInventory feeder = new MBItemInventory(this, 9).inputOnly();

    public TileBeehiveFeeder(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileBeehiveFeeder.class, TileBeehiveFeeder::new, MBSingletons.MODULAR_FEEDER), pos, state);
    }

    public FeedSlot checkFlower(Bee bee) {
        for (int x : this.getRandomSlots()) {
            var item = this.feeder.getStackInSlot(x);
            if (item.isEmpty()) {
                continue;
            }
            var block = GameUtil.getBlockFromItem(item);
            if (bee instanceof ProductiveBee productiveBee) {
                if (bee instanceof ConfigurableBee cb && "entity_types".equals(cb.getFlowerType())) {
                    if (this.checkEntity(cb, item, block)) {
                        return new FeedSlot(FeedResult.NON_CONSUME, this.feeder, x);
                    } else {
                        return FeedSlot.FAIL;
                    }
                }
                if (productiveBee.isFlowerBlock(block) || productiveBee.isFlowerItem(item)) {
                    return new FeedSlot(FeedResult.NON_CONSUME, this.feeder, x);
                }
                if (bee instanceof RancherBee) {
                    var flowerTag = item.get(DataComponents.ENTITY_DATA);
                    if (block.getBlock() instanceof Amber && flowerTag != null) {
                        var entity = AmberBlockEntity.createEntity(this.level, flowerTag.copyTag());
                        if (entity != null && entity.getType().is(ModTags.RANCHABLES)) {
                            return new FeedSlot(FeedResult.NON_CONSUME, this.feeder, x);
                        }
                    }
                }
            } else {
                if (block.is(BlockTags.FLOWERS)) {
                    return new FeedSlot(FeedResult.NON_CONSUME, this.feeder, x);
                }
            }
            var fluid = GameUtil.getFluidFromItem(item);
            if (!fluid.isEmpty()) {
                var fluidBlock = fluid.getFluid().defaultFluidState().createLegacyBlock();
                if (bee instanceof ProductiveBee productiveBee) {
                    if (productiveBee.isFlowerBlock(fluidBlock)) {
                        return new FeedSlot(FeedResult.NON_CONSUME, this.feeder, x);
                    }
                }
            }
        }
        return FeedSlot.FAIL;
    }

    private boolean checkEntity(ConfigurableBee bee, ItemStack flower, BlockState block) {
        var beeTag = bee.getNBTData();
        var flowerTag = flower.get(DataComponents.ENTITY_DATA);
        if (beeTag != null && flowerTag != null && beeTag.contains("flowerTag") && block.getBlock() instanceof Amber) {
            var tag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(beeTag.getString("flowerTag")));
            var entity = AmberBlockEntity.createEntity(this.level, flowerTag.copyTag());
            return entity != null && entity.getType().is(tag);
        }
        return false;
    }

    private IntList getRandomSlots() {
        IntList slots = new IntArrayList(IntStream.range(0, this.feeder.getSlots()).iterator());
        Collections.shuffle(slots);
        return slots;
    }

    @Override
    public void saveTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.saveTag(data, provider);
        data.put("feeder", this.feeder.serializeNBT(provider));
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.@NotNull Provider provider) {
        super.loadTag(data, provider);
        this.feeder.deserializeNBT(provider, data.getCompound("feeder"));
    }

    @Override
    public MBItemInventory getHandlerByName(String name) {
        return this.feeder;
    }

    @Override
    public void addInventoryDrops(Level level, @NotNull BlockPos pos, List<ItemStack> drops) {
        drops.addAll(this.feeder.toList());
    }

    @Override
    public IItemHandler getItemInventory() {
        return this.feeder;
    }

    @Override
    public void onChange(IItemHandler inv, int slot) {
        if (inv == this.feeder) {
            if (this.isActive() && this.core instanceof TileModularBeehive hive) {
                hive.onFeederChange(inv, slot);
            }
        }
    }

    public record FeedSlot(FeedResult result, IItemHandler inv, int slot) {

        public static FeedSlot FAIL = new FeedSlot(FeedResult.FAIL, null, -1);

        public boolean isSuccess() {
            return this.result != FeedResult.FAIL;
        }

    }

    public enum FeedResult {

        FAIL, NON_CONSUME, TRANSFORM

    }

}
