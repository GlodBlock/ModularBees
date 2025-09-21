package com.glodblock.github.modularbees.container;

import com.glodblock.github.modularbees.common.tileentities.hive.TileBeehiveAlveary;
import com.glodblock.github.modularbees.util.GameUtil;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.jetbrains.annotations.Nullable;

public class ContainerMBAlveary extends ContainerMBBase<TileBeehiveAlveary> {

    private final Slot input;
    private final Slot output;

    public final static MBGuiHandler.MenuHandler TYPE = MBGuiHandler.builder(TileBeehiveAlveary.class)
            .factory(ContainerMBAlveary::new)
            .build("modular_beehive_alveary");

    protected ContainerMBAlveary(@Nullable MenuType<?> type, int id, Inventory inv, TileBeehiveAlveary host) {
        super(type, id, inv, host);
        this.input = this.addSlot(host.getHandlerByName("in"), 0, 32, 36);
        this.output = this.addSlot(host.getHandlerByName("out"), 0, 129, 36);
        this.bindPlayerInventorySlots(inv);
        this.getSync().add(1, host::getBees, host::setBees, GameUtil.BEES_CODEC);
    }

    public boolean renderInputSlot() {
        if (this.input != null) {
            return !this.input.hasItem();
        }
        return false;
    }

    public boolean renderOutputSlot() {
        if (this.output != null) {
            return !this.output.hasItem();
        }
        return false;
    }

    @Nullable
    public Bee getInputBee() {
        var cage = this.input.getItem();
        if (!cage.isEmpty()) {
            return BeeCage.getEntityFromStack(cage, this.getPlayer().level(), true);
        }
        return null;
    }

    public BeehiveBlockEntity.BeeData getBee(int x) {
        return this.getHost().getBee(x);
    }

    @Override
    int getHeight() {
        return 166;
    }

    @Override
    int getWidth() {
        return 176;
    }

}
