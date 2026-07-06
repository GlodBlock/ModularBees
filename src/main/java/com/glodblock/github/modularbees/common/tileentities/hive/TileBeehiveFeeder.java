package com.glodblock.github.modularbees.common.tileentities.hive;

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
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class TileBeehiveFeeder extends TileBeehivePart implements ItemHandlerHost, SlotListener {

    protected final MBItemInventory feeder = new MBItemInventory(this, 9).inputOnly();

    public TileBeehiveFeeder(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public FeedSlot checkFlower(Bee bee) {
        for (int x : this.getRandomSlots()) {
            var item = this.feeder.getItemStack(x);
            if (item.isEmpty()) {
                continue;
            }
            var block = GameUtil.getBlockFromItem(item);
            if (bee instanceof ProductiveBee productiveBee) {
                if (bee instanceof ConfigurableBee cb && "entity_types".equals(cb.getFlowerType())) {
                    if (this.checkEntity(cb, item, block)) {
                        return new FeedSlot(FeedResult.NON_CONSUME, this.feeder, x);
                    }
                }
                if (productiveBee.isFlowerBlock(block) || productiveBee.isFlowerItem(item)) {
                    return new FeedSlot(FeedResult.NON_CONSUME, this.feeder, x);
                }
                if (bee instanceof RancherBee) {
                    var flowerTag = item.get(DataComponents.ENTITY_DATA);
                    if (block.getBlock() instanceof Amber && flowerTag != null) {
                        var entity = AmberBlockEntity.createEntity(this.level, flowerTag.copyTagWithoutId());
                        if (entity != null && entity.is(ModTags.RANCHABLES)) {
                            return new FeedSlot(FeedResult.NON_CONSUME, this.feeder, x);
                        }
                    }
                }
            } else {
                if (block.is(BlockTags.FLOWERS)) {
                    return new FeedSlot(FeedResult.NON_CONSUME, this.feeder, x);
                }
            }
            var fluid = GameUtil.getFluidFromItem(item, ItemAccess.forHandlerIndex(this.feeder, x));
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
        var beeTag = bee.getBeeData();
        var flowerTag = flower.get(DataComponents.ENTITY_DATA);
        if (beeTag != null && flowerTag != null && beeTag.flowerTag().isPresent() && block.getBlock() instanceof Amber) {
            var tag = TagKey.create(Registries.ENTITY_TYPE, Identifier.parse(beeTag.flowerTag().get()));
            var entity = AmberBlockEntity.createEntity(this.level, flowerTag.copyTagWithoutId());
            return entity != null && entity.is(tag);
        }
        return false;
    }

    private IntList getRandomSlots() {
        IntList slots = new IntArrayList(IntStream.range(0, this.feeder.size()).iterator());
        Collections.shuffle(slots);
        return slots;
    }

    @Override
    public void saveTag(ValueOutput data) {
        super.saveTag(data);
        this.feeder.serialize(data.child("feeder"));
    }

    @Override
    public void loadTag(ValueInput data) {
        super.loadTag(data);
        data.child("feeder").ifPresent(this.feeder::deserialize);
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
    public MBItemInventory getItemInventory() {
        return this.feeder;
    }

    @Override
    public void onChange(ResourceHandler<@NotNull ItemResource> inv, int slot) {
        if (inv == this.feeder) {
            if (this.isActive() && this.core instanceof TileModularBeehive hive) {
                hive.onFeederChange(inv, slot);
            }
        }
    }

    public record FeedSlot(FeedResult result, ResourceHandler<@NotNull ItemResource> inv, int slot) {

        public static FeedSlot FAIL = new FeedSlot(FeedResult.FAIL, null, -1);

        public boolean isSuccess() {
            return this.result != FeedResult.FAIL;
        }

    }

    public enum FeedResult {

        FAIL, NON_CONSUME, TRANSFORM

    }

}
