package com.glodblock.github.modularbees.client.gui.elements;

import com.glodblock.github.modularbees.client.util.RelativeRect2i;
import com.glodblock.github.modularbees.client.util.TooltipElement;
import cy.jdkdigital.productivebees.client.render.ingredient.BeeRenderer;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredientFactory;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BeeDisplay extends RelativeRect2i implements Renderable, TooltipElement {

    protected final Supplier<BeehiveBlockEntity.BeeData> supplier;

    public BeeDisplay(Supplier<BeehiveBlockEntity.BeeData> supplier) {
        this.supplier = supplier;
    }

    @Nullable
    private BeeInfo getInfo() {
        var data = this.supplier.get();
        if (data == null) {
            return null;
        }
        var tag = data.toOccupant().entityData().copyTag();
        String type = tag.getString("type");
        if (type.isEmpty() || type.equals("minecraft:")) {
            type = tag.getString("id");
        }
        var bee = BeeIngredientFactory.getIngredient(type).get();
        if (bee == null) {
            return null;
        }
        return new BeeInfo(bee, tag);
    }

    @Override
    public List<Component> getTooltipMessage(boolean isShift) {
        var info = this.getInfo();
        if (info != null) {
            var bee = info.bee.getCachedEntity(Minecraft.getInstance().level);
            List<Component> tooltip = new ArrayList<>();
            if (bee != null && bee.getEncodeId() != null) {
                if (bee instanceof ConfigurableBee && info.tag.contains("type")) {
                    ((ConfigurableBee) bee).setBeeType(info.tag.getString("type"));
                }
                tooltip.add(bee.getName());
                if (isShift) {
                    BeeHelper.populateBeeInfoFromTag(info.tag, tooltip);
                } else {
                    tooltip.add(Component.translatable("productivebees.information.hold_shift"));
                }
                return tooltip;
            }
        }
        return List.of();
    }

    @Override
    public boolean shouldDisplay(int mouseX, int mouseY) {
        return this.contains(mouseX, mouseY);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        var info = this.getInfo();
        if (info != null) {
            BeeRenderer.render(graphics, this.getX(), this.getY(), info.bee, Minecraft.getInstance());
        }
    }

    record BeeInfo(BeeIngredient bee, CompoundTag tag) {

    }

}
