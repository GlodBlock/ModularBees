package com.glodblock.github.modularbees.util;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public record TryResult(boolean ok, @Nullable Component message, Object info) {

    public static final TryResult SUCCESS = new TryResult(true, null, null);
    public static final TryResult FAILURE = new TryResult(false, null, null);

    public static TryResult fail(String translate) {
        return new TryResult(false, Component.translatable(translate), null);
    }

    public static TryResult fail(String translate, Object info, Object... args) {
        return new TryResult(false, Component.translatable(translate, args), info);
    }

}
