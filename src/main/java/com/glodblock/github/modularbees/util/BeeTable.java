package com.glodblock.github.modularbees.util;

import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveAlveary;
import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveFeeder;
import com.mojang.authlib.GameProfile;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.Amber;
import cy.jdkdigital.productivebees.common.block.entity.AmberBlockEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.recipe.AdvancedBeehiveRecipe;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeHelper;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class BeeTable {

    private final IdentityHashMap<IItemHandler, Int2ReferenceMap<List<BeeCache>>> index = new IdentityHashMap<>();
    private final List<BeeCache> data = new ArrayList<>();
    private final Function<TileBeehiveAlveary.AlvearyBee, TileBeehiveFeeder.FeedSlot> linker;
    private final BlockEntity host;
    private int dragonCount = 0;

    public BeeTable(BlockEntity host, Function<TileBeehiveAlveary.AlvearyBee, TileBeehiveFeeder.FeedSlot> linker) {
        this.host = host;
        this.linker = linker;
    }

    @SuppressWarnings("deprecation")
    public void loadBee(TileBeehiveAlveary.AlvearyBee beeData) {
        var tag = beeData.toOccupant().entityData().getUnsafe();
        if (tag.getString("type").equals("productivebees:draconic")) {
            this.dragonCount ++;
        }
        var cache = new BeeCache(beeData, this.host);
        this.data.add(cache);
        this.add(null, -1, cache);
    }

    public List<BeeCache> getData() {
        return this.data;
    }

    public void link(BeeCache cache) {
        var result = this.linker.apply(cache.key);
        if (result.isSuccess()) {
            this.remove(cache);
            this.add(result.inv(), result.slot(), cache);
            cache.setIndex(result.inv(), result.slot());
        } else {
            this.remove(cache);
            this.add(null, -1, cache);
            cache.setIndex(null, -1);
        }
    }

    public void collectOutput(Level world, Consumer<ItemStack> collector) {
        for (var cache : this.data) {
            if (cache.needLookup()) {
                this.link(cache);
            }
            var output = cache.getOutput();
            output.output(collector, world.getRandom());
        }
    }

    public void feederUpdate(IItemHandler inv, int slot) {
        if (inv != null && slot != -1) {
            this.feederUpdate(null, -1);
        }
        var map = this.index.get(inv);
        if (map != null) {
            var list = map.get(slot);
            if (list != null) {
                list.forEach(BeeCache::unbind);
            }
        }
    }

    public int getBeeCount() {
        return this.data.size();
    }

    public int getWorkingBee() {
        var idle = this.index.get(null);
        if (idle == null) {
            return this.data.size();
        }
        return this.data.size() - idle.getOrDefault(-1, List.of()).size();
    }

    public int getDragonBee() {
        return this.dragonCount;
    }

    private void add(IItemHandler handler, int slot, BeeCache cache) {
        var map = this.index.computeIfAbsent(handler, k -> new Int2ReferenceOpenHashMap<>());
        var list = map.computeIfAbsent(slot, k -> new ArrayList<>());
        list.add(cache);
    }

    private void remove(BeeCache cache) {
        this.remove(cache.inv, cache.slot, cache);
    }

    private void remove(IItemHandler handler, int slot, BeeCache cache) {
        var map = this.index.computeIfAbsent(handler, k -> new Int2ReferenceOpenHashMap<>());
        var list = map.computeIfAbsent(slot, k -> new ArrayList<>());
        list.remove(cache);
    }

    public void clear() {
        this.data.clear();
        this.index.clear();
        this.dragonCount = 0;
    }

    private record Output(List<ChanceStack> outputs, int geneBoost) {

        static Output EMPTY = new Output(List.of(), 0);

        void output(Consumer<ItemStack> collector, RandomSource random) {
            final Consumer<ItemStack> booster = this::applyBoost;
            this.outputs.forEach(c -> c.get(booster.andThen(collector), random));
        }

        void applyBoost(ItemStack stack) {
            if (this.geneBoost > 0 && !stack.isEmpty()) {
                if (stack.getCount() == 1) {
                    stack.grow(this.geneBoost);
                } else {
                    var multi = (1.0F / (this.geneBoost + 2.0F) + (this.geneBoost + 1.0F) / 2.0F) * stack.getCount();
                    stack.grow(Math.round(multi));
                }
            }
        }

    }

    public static class BeeCache {

        private static final Set<String> SPECIAL_BEES = Set.of("productivebees:lumber_bee", "productivebees:quarry_bee", "productivebees:dye_bee", "productivebees:wanna");
        private final TileBeehiveAlveary.AlvearyBee key;
        private final String beeId;
        private Output output = null;
        private Supplier<Output> special = () -> Output.EMPTY;
        IItemHandler inv = null;
        int slot = -1;
        boolean needLookup = true;
        int geneBoost = 0;

        BeeCache(TileBeehiveAlveary.AlvearyBee key, BlockEntity host) {
            this.key = key;
            var world = host.getLevel();
            if (world != null) {
                var entity = key.toOccupant().createEntity(world, host.getBlockPos());
                if (entity instanceof Bee bee) {
                    if (entity.hasData(ProductiveBees.ATTRIBUTE_HANDLER)) {
                        this.geneBoost = entity.getData(ProductiveBees.ATTRIBUTE_HANDLER).getAttributeValue(GeneAttribute.PRODUCTIVITY).getValue();
                    } else {
                        this.geneBoost = 0;
                    }
                    this.beeId = (bee instanceof ProductiveBee cBee) ? Objects.requireNonNull(cBee.getBeeType()).toString() : bee.getEncodeId();
                    var hiveRecipe = this.lookupRecipe(world);
                    if (hiveRecipe != null) {
                        var main = hiveRecipe.value()
                                .getRecipeOutputs()
                                .entrySet()
                                .stream()
                                .map(e -> ChanceStack.of(e.getKey(), e.getValue()));
                        this.output = new Output(main.toList(), this.geneBoost);
                    } else if (bee instanceof ProductiveBee) {
                        if (SPECIAL_BEES.contains(this.beeId)) {
                            this.special = () -> this.lookupSpecialOutput(world, host.getBlockPos());
                        }
                    }
                } else {
                    this.beeId = "";
                }
            } else {
                this.beeId = "";
            }
        }

        RecipeHolder<AdvancedBeehiveRecipe> lookupRecipe(Level world) {
            List<RecipeHolder<AdvancedBeehiveRecipe>> allRecipes = world.getRecipeManager().getAllRecipesFor(ModRecipeTypes.ADVANCED_BEEHIVE_TYPE.get());
            var beeInv = new BeeHelper.IdentifierInventory(this.beeId);
            for (RecipeHolder<AdvancedBeehiveRecipe> recipe : allRecipes) {
                if (recipe.value().matches(beeInv, world)) {
                    return recipe;
                }
            }
            return null;
        }

        public String getID() {
            return this.beeId;
        }

        @NotNull
        Output lookupSpecialOutput(Level world, BlockPos pos) {
            if (!this.isBind()) {
                return Output.EMPTY;
            }
            var input = this.inv.getStackInSlot(this.slot);
            if (input.isEmpty()) {
                return Output.EMPTY;
            }
            final var inputBlock = GameUtil.getBlockFromItem(input);
            switch (this.beeId) {
                case "productivebees:lumber_bee" -> {
                    if (inputBlock.is(ModTags.LUMBER) && !inputBlock.is(ModTags.DUPE_BLACKLIST)) {
                        return new Output(List.of(new ChanceStack.CommonStack(input.copyWithCount(1))), this.geneBoost);
                    }
                }
                case "productivebees:quarry_bee" -> {
                    if (inputBlock.is(ModTags.QUARRY) && !inputBlock.is(ModTags.DUPE_BLACKLIST)) {
                        return new Output(List.of(new ChanceStack.CommonStack(input.copyWithCount(1))), this.geneBoost);
                    }
                }
                case "productivebees:dye_bee" -> {
                    if (inputBlock.is(BlockTags.FLOWERS)) {
                        var dye = BeeHelper.getRecipeOutputFromInput(world, input.getItem());
                        if (!dye.isEmpty()) {
                            return new Output(List.of(new ChanceStack.CommonStack(dye.copy())), this.geneBoost);
                        }
                    }
                }
                case "productivebees:wanna" -> {
                    if (inputBlock.getBlock() instanceof Amber) {
                        var tag = input.get(DataComponents.ENTITY_DATA);
                        if (tag != null) {
                            var entity = AmberBlockEntity.createEntity(world, tag.copyTag());
                            if (entity instanceof Mob mob && world instanceof ServerLevel serverWorld) {
                                var loot = serverWorld.getServer().reloadableRegistries().getLootTable(mob.getLootTable());
                                return new Output(List.of(new MobOutput(mob, loot, serverWorld, pos, 1)), this.geneBoost);
                            }
                        }
                    }
                }
            }
            return Output.EMPTY;
        }

        void setIndex(IItemHandler inv, int slot) {
            this.inv = inv;
            this.slot = slot;
            this.needLookup = false;
        }

        void unbind() {
            this.inv = null;
            this.slot = -1;
            this.needLookup = true;
        }

        public boolean needLookup() {
            return this.needLookup;
        }

        Output getOutput() {
            if (this.isBind()) {
                if (this.output != null) {
                    return this.output;
                } else {
                    return this.special.get();
                }
            }
            return Output.EMPTY;
        }

        public boolean isBind() {
            return this.inv != null && this.slot >= 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (obj.getClass() == BeeCache.class) {
                return ((BeeCache) obj).key.equals(this.key);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.key.hashCode();
        }

        record MobOutput(Mob mob, LootTable loot, ServerLevel world, BlockPos pos, int multiplier) implements ChanceStack {

            @Override
            public void get(Consumer<ItemStack> adder, RandomSource random) {
                if (this.loot != LootTable.EMPTY) {
                    var fakePlayer = FakePlayerFactory.get(this.world, new GameProfile(ModEntities.WANNA_BEE_UUID, "wanna_bee"));
                    var lootContextBuilder = new LootParams.Builder(this.world);
                    lootContextBuilder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, fakePlayer);
                    lootContextBuilder.withParameter(LootContextParams.ORIGIN, this.pos.getCenter());
                    lootContextBuilder.withParameter(LootContextParams.DAMAGE_SOURCE, this.world.damageSources().generic());
                    lootContextBuilder.withParameter(LootContextParams.TOOL, new ItemStack(Items.DIAMOND_AXE));
                    lootContextBuilder.withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, fakePlayer);
                    lootContextBuilder.withOptionalParameter(LootContextParams.ATTACKING_ENTITY, fakePlayer);
                    lootContextBuilder.withParameter(LootContextParams.THIS_ENTITY, this.mob);
                    var items = this.loot.getRandomItems(lootContextBuilder.create(LootContextParamSets.ENTITY));
                    var size = items.size();
                    if (size > 0) {
                        items.stream()
                                .skip(random.nextInt(size))
                                .filter(stack -> !stack.is(ModTags.WANNABEE_LOOT_BLACKLIST))
                                .map(stack -> stack.copyWithCount(stack.getCount() * this.multiplier))
                                .findAny()
                                .ifPresent(adder);
                    }
                }
            }

        }

    }

}
