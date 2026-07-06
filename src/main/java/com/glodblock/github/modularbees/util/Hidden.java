package com.glodblock.github.modularbees.util;

public interface Hidden {

    static boolean visible(Object obj) {
        return !(obj instanceof Hidden);
    }

}
