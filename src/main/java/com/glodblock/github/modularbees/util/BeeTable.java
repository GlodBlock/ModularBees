package com.glodblock.github.modularbees.util;

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
import cy.jdkdigital.productivelib.common.recipe.TagOutputRecipe;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
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
import java.util.stream.Stream;

public final class BeeTable {

    private final IdentityHashMap<IItemHandler, Int2ReferenceMap<List<BeeCache>>> index = new IdentityHashMap<>();
    private final List<BeeCache> data = new ArrayList<>();
    private final Function<BeehiveBlockEntity.BeeData, TileBeehiveFeeder.FeedSlot> linker;
    private final BlockEntity host;
    private int dragonCount = 0;

    public BeeTable(BlockEntity host, Function<BeehiveBlockEntity.BeeData, TileBeehiveFeeder.FeedSlot> linker) {
        this.host = host;
        this.linker = linker;
    }

    @SuppressWarnings("deprecation")
    public void loadBee(BeehiveBlockEntity.BeeData beeData) {
        var tag = beeData.toOccupant().entityData().getUnsafe();
        if (tag.getString("type").equals("productivebees:draconic")) {
            this.dragonCount ++;
        }
        var cache = new BeeCache(beeData, this.host);
        this.data.add(cache);
        this.add(null, -1, cache);
    }

    public void collectOutput(Level world, Consumer<ItemStack> collector) {
        for (var cache : this.data) {
            if (cache.needLookup()) {
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

    private record Output(List<ChanceStack> outputs) {

        static Output EMPTY = new Output(List.of());

        void output(Consumer<ItemStack> collector, RandomSource random) {
            this.outputs.forEach(c -> c.get(collector, random));
        }

    }

    private static class BeeCache {

        private static final Set<String> SPECIAL_BEES = Set.of("productivebees:lumber_bee", "productivebees:quarry_bee", "productivebees:dye_bee", "productivebees:wanna");
        private final BeehiveBlockEntity.BeeData key;
        private final String beeId;
        private Output output = null;
        private Supplier<Output> special = () -> Output.EMPTY;
        IItemHandler inv = null;
        int slot = -1;
        boolean needLookup = true;
        float geneBoost = 0;

        BeeCache(BeehiveBlockEntity.BeeData key, BlockEntity host) {
            this.key = key;
            var world = host.getLevel();
            if (world != null) {
                var entity = key.toOccupant().createEntity(world, host.getBlockPos());
                if (entity instanceof Bee bee) {
                    if (entity.hasData(ProductiveBees.ATTRIBUTE_HANDLER)) {
                        var gene = entity.getData(ProductiveBees.ATTRIBUTE_HANDLER).getAttributeValue(GeneAttribute.PRODUCTIVITY).getValue();
                        if (gene == 0) {
                            this.geneBoost = 0;
                        } else if (gene == 1) {
                            this.geneBoost = 1;
                        } else {
                            this.geneBoost = 1F / (gene + 2F) + (gene + 1F) / 2F;
                        }
                    }
                    this.beeId = (bee instanceof ProductiveBee cBee) ? Objects.requireNonNull(cBee.getBeeType()).toString() : bee.getEncodeId();
                    var hiveRecipe = this.lookupRecipe(world);
                    if (hiveRecipe != null) {
                        var main = hiveRecipe.value()
                                .getRecipeOutputs()
                                .entrySet()
                                .stream()
                                .map(e -> ChanceStack.of(e.getKey(), this.add(e.getValue())));
                        var partial = this.geneBoost % 1;
                        Stream<ChanceStack> left = Stream.empty();
                        if (!Mth.equal(partial, 0)) {
                            left = hiveRecipe.value()
                                    .getRecipeOutputs()
                                    .entrySet()
                                    .stream()
                                    .map(e -> ChanceStack.of(e.getKey(), this.partial(e.getValue(), partial)));
                        }
                        this.output = new Output(Stream.concat(main, left).toList());
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

        private TagOutputRecipe.ChancedOutput add(TagOutputRecipe.ChancedOutput origin) {
            if (this.geneBoost > 0) {
                return new TagOutputRecipe.ChancedOutput(origin.ingredient(), (int) (origin.min() + this.geneBoost), (int) (origin.max() + this.geneBoost), origin.chance());
            }
            return origin;
        }

        private TagOutputRecipe.ChancedOutput partial(TagOutputRecipe.ChancedOutput origin, float partial) {
            return new TagOutputRecipe.ChancedOutput(origin.ingredient(), 1, 1, partial * origin.chance());
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
            float partial = this.geneBoost % 1;
            switch (this.beeId) {
                case "productivebees:lumber_bee" -> {
                    if (inputBlock.is(ModTags.LUMBER) && !inputBlock.is(ModTags.DUPE_BLACKLIST)) {
                        return new Output(List.of(
                                new ChanceStack.CommonStack(input.copyWithCount(1 + (int) this.geneBoost)),
                                new ChanceStack.ChanceStackImpl(input.copyWithCount(1), partial)
                        ));
                    }
                }
                case "productivebees:quarry_bee" -> {
                    if (inputBlock.is(ModTags.QUARRY) && !inputBlock.is(ModTags.DUPE_BLACKLIST)) {
                        return new Output(List.of(
                                new ChanceStack.CommonStack(input.copyWithCount(1 + (int) this.geneBoost)),
                                new ChanceStack.ChanceStackImpl(input.copyWithCount(1), partial)
                        ));
                    }
                }
                case "productivebees:dye_bee" -> {
                    if (inputBlock.is(BlockTags.FLOWERS)) {
                        var dye = BeeHelper.getRecipeOutputFromInput(world, input.getItem());
                        if (!dye.isEmpty()) {
                            dye.setCount((int) (dye.getCount() + this.geneBoost));
                            return new Output(List.of(
                                    new ChanceStack.CommonStack(dye),
                                    new ChanceStack.ChanceStackImpl(dye.copyWithCount(1), partial)
                            ));
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
                                return new Output(List.of(
                                        new MobOutput(mob, loot, serverWorld, pos, (int) this.geneBoost),
                                        ChanceStack.partial(new MobOutput(mob, loot, serverWorld, pos, 1), partial)
                                ));
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

        boolean needLookup() {
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

        boolean isBind() {
            return this.inv != null && this.slot >= 0;
        }

        @Override
        public boolean equals(Object obj) {
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
                                .filter(stack -> !stack.is(ModTags.WANNABEE_LOOT_BLACKLIST))
                                .peek(stack -> stack.setCount(stack.getCount() * this.multiplier))
                                .skip(random.nextInt(size))
                                .findAny()
                                .ifPresent(adder);
                    }
                }
            }

        }

    }

}
