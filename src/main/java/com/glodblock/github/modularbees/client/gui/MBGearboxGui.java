package com.glodblock.github.modularbees.client.gui;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.gui.elements.EnergyDisplay;
import com.glodblock.github.modularbees.client.util.PicData;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.tileentities.centrifuge.TileCentrifugeGearbox;
import com.glodblock.github.modularbees.container.ContainerMBGearbox;
import com.glodblock.github.modularbees.util.GameUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.function.IntSupplier;

public class MBGearboxGui extends MBBaseGui<ContainerMBGearbox> {

    private static final PicData BG = PicData.of(ModularBees.id("textures/gui/gearbox.png")).select(0, 0, 176, 166);

    public MBGearboxGui(ContainerMBGearbox container, Inventory inv, Component component) {
        super(container, inv, component);
        var wax = new WaxDisplay(container::getWax)
                .capacity(TileCentrifugeGearbox.MAX_WAX)
                .texture(PicData.of(ModularBees.id("textures/gui/gearbox.png")).select(176, 0, 11, 52));
        wax.setPosition(43, 17);
        wax.setSize(11, 52);
        this.group.add("wax", wax);
    }

    @Override
    protected PicData getBackground() {
        return BG;
    }

    @Override
    protected Component getGuiName() {
        return MBSingletons.MODULAR_CENTRIFUGE_GEARBOX.getName();
    }

    private static class WaxDisplay extends EnergyDisplay {

        public WaxDisplay(IntSupplier wax) {
            super(wax);
        }

        @Override
        public List<Component> getTooltipMessage(boolean isShift) {
            return List.of(Component.translatable("modularbees.gui.wax.amount", GameUtil.NUMBER_F.format(this.energy.getAsInt()), GameUtil.NUMBER_F.format(this.capacity)));
        }

    }

}
