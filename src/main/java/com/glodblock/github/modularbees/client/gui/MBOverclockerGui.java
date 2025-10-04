package com.glodblock.github.modularbees.client.gui;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.client.gui.elements.EnergyDisplay;
import com.glodblock.github.modularbees.client.util.PicData;
import com.glodblock.github.modularbees.common.MBSingletons;
import com.glodblock.github.modularbees.common.recipe.ElectrodeRecipe;
import com.glodblock.github.modularbees.container.ContainerMBOverclocker;
import com.glodblock.github.modularbees.util.GameUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class MBOverclockerGui extends MBBaseGui<ContainerMBOverclocker> {

    private static final PicData BG = PicData.of(ModularBees.id("textures/gui/overclocker.png")).select(0, 0, 176, 166);

    public MBOverclockerGui(ContainerMBOverclocker container, Inventory inv, Component component) {
        super(container, inv, component);
        var energy = new EnergyDisplay(container::getEnergy)
                .capacity(container.getHost().getEnergyStorage().getMaxEnergyStored())
                .texture(PicData.of(ModularBees.id("textures/gui/overclocker.png")).select(176, 0, 11, 52));
        energy.setPosition(43, 17);
        energy.setSize(11, 52);
        this.group.add("energy", energy);
    }

    @Override
    protected PicData getBackground() {
        return BG;
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        var stack = this.getMenu().getHost().getItemInventory().getStackInSlot(0);
        if (stack.isEmpty()) {
            this.drawStringCenter(graphics, Component.translatable("modularbees.gui.modular_beehive_overclocker.empty"), 135, 39);
        } else {
            var item = stack.getItem();
            var recipe = ElectrodeRecipe.getCache(this.getMenu().getPlayer().level()).get(item);
            if (recipe != null) {
                this.drawStringCenter(graphics, Component.translatable("modularbees.gui.modular_beehive_overclocker.boost"), 135, 33);
                this.drawStringCenter(graphics, "x" + GameUtil.NUMBER_F.format(recipe.power()), 132, 44);
            } else {
                this.drawStringCenter(graphics, Component.translatable("modularbees.gui.modular_beehive_overclocker.empty"), 135, 39);
            }
        }
    }

    @Override
    protected Component getGuiName() {
        return MBSingletons.MODULAR_OVERCLOCKER.getName();
    }

}
