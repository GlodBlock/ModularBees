package com.glodblock.github.modularbees.common.inventory;

public enum IO {
    IN, OUT, ALL;

    public boolean canInsert() {
        return this != OUT;
    }

    public boolean canExtract() {
        return this != IN;
    }

}
