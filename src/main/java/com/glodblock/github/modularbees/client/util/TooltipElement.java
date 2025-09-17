package com.glodblock.github.modularbees.client.util;

import net.minecraft.network.chat.Component;

import java.util.List;

public interface TooltipElement {

    List<Component> getTooltipMessage(boolean isShift);

    boolean shouldDisplay(int mouseX, int mouseY);

}
