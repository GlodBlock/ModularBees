package com.glodblock.github.modularbees.jei;

import com.glodblock.github.modularbees.client.gui.MBBaseGui;
import com.glodblock.github.modularbees.container.slot.DisplaySlot;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GhostSlotHandler<T extends MBBaseGui<?>> implements IGhostIngredientHandler<@NotNull T> {

    @Override
    public <I> @NotNull List<Target<@NotNull I>> getTargetsTyped(@NotNull T gui, ITypedIngredient<@NotNull I> ingredient, boolean doStart) {
        var wrapped = wrapDraggedItem(ingredient.getIngredient());
        if (wrapped == null) {
            return List.of();
        }
        List<Target<@NotNull I>> targets = new ArrayList<>();
        addItemStackTargets(gui, targets);
        return targets;
    }

    @Nullable
    private static <T> ItemStack wrapDraggedItem(T ingredient) {
        return VanillaTypes.ITEM_STACK.castIngredient(ingredient).orElse(null);
    }

    /**
     * Returns possible drop-targets for ghost items.
     */
    private static <I> void addItemStackTargets(MBBaseGui<?> gui, List<Target<@NotNull I>> targets) {
        for (var slot : gui.getMenu().slots) {
            if (slot.isActive() && slot instanceof DisplaySlot fakeSlot) {
                targets.add(new ItemSlotTarget<>(gui, fakeSlot));
            }
        }
    }

    @Override
    public void onComplete() {
    }

    private static class ItemSlotTarget<I> implements Target<@NotNull I> {

        private final MBBaseGui<?> gui;
        private final DisplaySlot slot;
        private final Rect2i area;

        public ItemSlotTarget(MBBaseGui<?> screen, DisplaySlot slot) {
            this.gui = screen;
            this.slot = slot;
            this.area = new Rect2i(screen.getLeftPos() + slot.x, screen.getTopPos() + slot.y, 16, 16);
        }

        @Override
        public @NotNull Rect2i getArea() {
            return area;
        }

        @Override
        public void accept(@NotNull I ingredient) {
            var wrapped = wrapDraggedItem(ingredient);
            if (wrapped != null) {
                this.slot.set(wrapped);
                this.gui.sendAction("jei_slot", this.slot.index, wrapped);
            }
        }
    }

}