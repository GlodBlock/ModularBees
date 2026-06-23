package com.glodblock.github.modularbees.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public record RotorBlocks(Direction[] faces, DirectionProperty property) {

    public RotorBlocks(String id, Direction... faces) {
        this(faces, DirectionProperty.create(id, faces));
    }

    public Direction defaultFace() {
        return this.faces[0];
    }

    public boolean isNone() {
        return this == NONE;
    }

    public boolean validFace(Direction face) {
        if (this.isNone()) {
            return false;
        }
        for (Direction direction : this.faces) {
            if (direction == face) {
                return true;
            }
        }
        return false;
    }

    public static final RotorBlocks NONE = new RotorBlocks("facing", Direction.NORTH);
    public static final RotorBlocks HORIZONTAL = new RotorBlocks("facing", Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);
    public static final RotorBlocks ALL = new RotorBlocks("facing", Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP, Direction.DOWN);

}
