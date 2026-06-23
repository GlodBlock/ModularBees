package com.glodblock.github.modularbees.client.model;

import com.glodblock.github.modularbees.client.util.ConnectBlock;
import com.glodblock.github.modularbees.client.util.StandardItemTransform;
import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;

public abstract class ConnectBorderlineBakedModel implements IDynamicBakedModel {

    private static final ChunkRenderTypeSet RENDER_TYPES = ChunkRenderTypeSet.of(RenderType.CUTOUT_MIPPED, RenderType.SOLID);
    protected static final Object2ReferenceMap<FaceCorner, List<Vector3f>> V_MAP = createVertexMap();
    protected static final EnumMap<Direction, List<Vector3f>> F_MAP = createFaceMap();
    public static final int LU = 0;
    public static final int RU = 1;
    public static final int LD = 2;
    public static final int RD = 4;
    public static final float EPS = 0.001F;
    protected final TextureAtlasSprite border;
    @NotNull
    protected final Transformation rotorMatrix;
    protected static final ModelProperty<Connect> CONNECT_STATE = new ModelProperty<>();

    public ConnectBorderlineBakedModel(Function<Material, TextureAtlasSprite> getter, ModelState modelTransform, ResourceLocation border) {
        this.border = getter.apply(new Material(InventoryMenu.BLOCK_ATLAS, border));
        var inverseMatrix = modelTransform.getRotation().inverse();
        this.rotorMatrix = inverseMatrix == null ? Transformation.identity() : inverseMatrix;
    }

    @Override
    @NotNull
    public ModelData getModelData(@NotNull BlockAndTintGetter world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        var connect = new Connect();
        var self = world.getBlockState(pos).getBlock();
        if (self instanceof ConnectBlock c) {
            for (int x = -1; x <= 1; x ++) {
                for (int y = -1; y <= 1; y ++) {
                    for (int z = -1; z <= 1; z ++) {
                        var offset = pos.offset(x, y, z);
                        if (c.canConnect(world, offset)) {
                            connect.set(x, y, z);
                        }
                    }
                }
            }
        }
        return modelData.derive().with(CONNECT_STATE, connect).build();
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction side, @NotNull RandomSource randomSource, @NotNull ModelData modelData, @Nullable RenderType renderType) {
        if (side == null) {
            return Collections.emptyList();
        }
        Connect connect;
        if (blockState == null) {
            connect = new Connect();
        } else {
            connect = modelData.get(CONNECT_STATE);
        }
        if (connect == null) {
            connect = new Connect();
        }
        List<BakedQuad> quads = new ArrayList<>();
        if (renderType == null || renderType == RenderType.CUTOUT_MIPPED) {
            this.addQuad(quads, side, connect.getIndex(side, LU), LU);
            this.addQuad(quads, side, connect.getIndex(side, RU), RU);
            this.addQuad(quads, side, connect.getIndex(side, LD), LD);
            this.addQuad(quads, side, connect.getIndex(side, RD), RD);
        }
        if (renderType == null || renderType == RenderType.SOLID) {
            this.addQuad(quads, side, connect.getBlocked(side));
        }
        return quads;
    }

    @Override
    public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        return RENDER_TYPES;
    }

    private List<Vector3f> calculateCorners(Direction face, int corner) {
        return V_MAP.get(new FaceCorner(face, corner));
    }

    private void addQuad(List<BakedQuad> quads, Direction side, boolean blocked) {
        if (blocked) {
            return;
        }
        var builder = new QuadBakingVertexConsumer();
        var sprite = getFaceSprite(this.rotorMatrix.rotateTransform(side));
        var cons = F_MAP.get(side);
        builder.setSprite(sprite);
        builder.setDirection(side);
        builder.setShade(true);
        builder.setHasAmbientOcclusion(true);
        var normal = side.getNormal();
        var c1 = cons.get(0);
        var c2 = cons.get(1);
        var c3 = cons.get(2);
        var c4 = cons.get(3);
        this.putVertex(builder, sprite, normal, c1.x(), c1.y(), c1.z(), 0, 0);
        this.putVertex(builder, sprite, normal, c2.x(), c2.y(), c2.z(), 0, 1);
        this.putVertex(builder, sprite, normal, c3.x(), c3.y(), c3.z(), 1, 1);
        this.putVertex(builder, sprite, normal, c4.x(), c4.y(), c4.z(), 1, 0);
        quads.add(builder.bakeQuad());
    }

    abstract TextureAtlasSprite getFaceSprite(Direction side);

    private void addQuad(List<BakedQuad> quads, Direction side, int index, int corner) {
        if (index < 0) {
            return;
        }
        var builder = new QuadBakingVertexConsumer();
        var cons = this.calculateCorners(side, corner);
        builder.setSprite(this.border);
        builder.setDirection(side);
        builder.setShade(true);
        var normal = side.getNormal();
        var c1 = cons.get(0);
        var c2 = cons.get(1);
        var c3 = cons.get(2);
        var c4 = cons.get(3);
        float u0 = this.getU0(index);
        float u1 = this.getU1(index);
        float v0 = this.getV0(index);
        float v1 = this.getV1(index);
        switch (corner) {
            case LU -> {
                this.putVertex(builder, this.border, normal, c1.x(), c1.y(), c1.z(), u0, v0);
                this.putVertex(builder, this.border, normal, c2.x(), c2.y(), c2.z(), u0, v1);
                this.putVertex(builder, this.border, normal, c3.x(), c3.y(), c3.z(), u1, v1);
                this.putVertex(builder, this.border, normal, c4.x(), c4.y(), c4.z(), u1, v0);
            }
            case RU -> {
                this.putVertex(builder, this.border, normal, c1.x(), c1.y(), c1.z(), u1, v0);
                this.putVertex(builder, this.border, normal, c2.x(), c2.y(), c2.z(), u1, v1);
                this.putVertex(builder, this.border, normal, c3.x(), c3.y(), c3.z(), u0, v1);
                this.putVertex(builder, this.border, normal, c4.x(), c4.y(), c4.z(), u0, v0);
            }
            case LD -> {
                this.putVertex(builder, this.border, normal, c1.x(), c1.y(), c1.z(), u0, v1);
                this.putVertex(builder, this.border, normal, c2.x(), c2.y(), c2.z(), u0, v0);
                this.putVertex(builder, this.border, normal, c3.x(), c3.y(), c3.z(), u1, v0);
                this.putVertex(builder, this.border, normal, c4.x(), c4.y(), c4.z(), u1, v1);
            }
            case RD -> {
                this.putVertex(builder, this.border, normal, c1.x(), c1.y(), c1.z(), u1, v1);
                this.putVertex(builder, this.border, normal, c2.x(), c2.y(), c2.z(), u1, v0);
                this.putVertex(builder, this.border, normal, c3.x(), c3.y(), c3.z(), u0, v0);
                this.putVertex(builder, this.border, normal, c4.x(), c4.y(), c4.z(), u0, v1);
            }
        }
        quads.add(builder.bakeQuad());
    }

    private static EnumMap<Direction, List<Vector3f>> createFaceMap() {
        EnumMap<Direction, List<Vector3f>> map = new EnumMap<>(Direction.class);
        map.put(Direction.EAST, List.of(new Vector3f(1, 1, 1), new Vector3f(1, 0, 1), new Vector3f(1, 0, 0), new Vector3f(1, 1, 0)));
        map.put(Direction.WEST, List.of(new Vector3f(0, 1, 1), new Vector3f(0, 0, 1), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0)).reversed());
        map.put(Direction.UP, List.of(new Vector3f(0, 1, 0), new Vector3f(0, 1, 1), new Vector3f(1, 1, 1), new Vector3f(1, 1, 0)));
        map.put(Direction.DOWN, List.of(new Vector3f(1, 0, 1), new Vector3f(1, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 1)).reversed());
        map.put(Direction.SOUTH, List.of(new Vector3f(0, 1, 1), new Vector3f(0, 0, 1), new Vector3f(1, 0, 1), new Vector3f(1, 1, 1)));
        map.put(Direction.NORTH, List.of(new Vector3f(0, 1, 0), new Vector3f(0, 0, 0), new Vector3f(1, 0, 0), new Vector3f(1, 1, 0)).reversed());
        return map;
    }

    // Corner vertex map with offset
    private static Object2ReferenceMap<FaceCorner, List<Vector3f>> createVertexMap() {
        Object2ReferenceMap<FaceCorner, List<Vector3f>> map = new Object2ReferenceOpenHashMap<>();
        map.put(new FaceCorner(Direction.EAST, LU), List.of(new Vector3f(1+EPS, 1+EPS, 1+EPS), new Vector3f(1+EPS, 0.5f, 1+EPS), new Vector3f(1+EPS, 0.5f, 0.5f), new Vector3f(1+EPS, 1+EPS, 0.5f)));
        map.put(new FaceCorner(Direction.EAST, RU), List.of(new Vector3f(1+EPS, 1+EPS, 0.5f), new Vector3f(1+EPS, 0.5f, 0.5f), new Vector3f(1+EPS, 0.5f, -EPS), new Vector3f(1+EPS, 1+EPS, -EPS)));
        map.put(new FaceCorner(Direction.EAST, LD), List.of(new Vector3f(1+EPS, 0.5f, 1+EPS), new Vector3f(1+EPS, -EPS, 1+EPS), new Vector3f(1+EPS, -EPS, 0.5f), new Vector3f(1+EPS, 0.5f, 0.5f)));
        map.put(new FaceCorner(Direction.EAST, RD), List.of(new Vector3f(1+EPS, 0.5f, 0.5f), new Vector3f(1+EPS, -EPS, 0.5f), new Vector3f(1+EPS, -EPS, -EPS), new Vector3f(1+EPS, 0.5f, -EPS)));
        map.put(new FaceCorner(Direction.WEST, LU), List.of(new Vector3f(-EPS, 1+EPS, -EPS), new Vector3f(-EPS, 0.5f, -EPS), new Vector3f(-EPS, 0.5f, 0.5f), new Vector3f(-EPS, 1+EPS, 0.5f)));
        map.put(new FaceCorner(Direction.WEST, RU), List.of(new Vector3f(-EPS, 1+EPS, 0.5f), new Vector3f(-EPS, 0.5f, 0.5f), new Vector3f(-EPS, 0.5f, 1+EPS), new Vector3f(-EPS, 1+EPS, 1+EPS)));
        map.put(new FaceCorner(Direction.WEST, LD), List.of(new Vector3f(-EPS, 0.5f, -EPS), new Vector3f(-EPS, -EPS, -EPS), new Vector3f(-EPS, -EPS, 0.5f), new Vector3f(-EPS, 0.5f, 0.5f)));
        map.put(new FaceCorner(Direction.WEST, RD), List.of(new Vector3f(-EPS, 0.5f, 0.5f), new Vector3f(-EPS, -EPS, 0.5f), new Vector3f(-EPS, -EPS, 1+EPS), new Vector3f(-EPS, 0.5f, 1+EPS)));
        map.put(new FaceCorner(Direction.SOUTH, LU), List.of(new Vector3f(-EPS, 1+EPS, 1+EPS), new Vector3f(-EPS, 0.5f, 1+EPS), new Vector3f(0.5f, 0.5f, 1+EPS), new Vector3f(0.5f, 1+EPS, 1+EPS)));
        map.put(new FaceCorner(Direction.SOUTH, RU), List.of(new Vector3f(0.5f, 1+EPS, 1+EPS), new Vector3f(0.5f, 0.5f, 1+EPS), new Vector3f(1+EPS, 0.5f, 1+EPS), new Vector3f(1+EPS, 1+EPS, 1+EPS)));
        map.put(new FaceCorner(Direction.SOUTH, LD), List.of(new Vector3f(-EPS, 0.5f, 1+EPS), new Vector3f(-EPS, -EPS, 1+EPS), new Vector3f(0.5f, -EPS, 1+EPS), new Vector3f(0.5f, 0.5f, 1+EPS)));
        map.put(new FaceCorner(Direction.SOUTH, RD), List.of(new Vector3f(0.5f, 0.5f, 1+EPS), new Vector3f(0.5f, -EPS, 1+EPS), new Vector3f(1+EPS, -EPS, 1+EPS), new Vector3f(1+EPS, 0.5f, 1+EPS)));
        map.put(new FaceCorner(Direction.NORTH, LU), List.of(new Vector3f(1+EPS, 1+EPS, -EPS), new Vector3f(1+EPS, 0.5f, -EPS), new Vector3f(0.5f, 0.5f, -EPS), new Vector3f(0.5f, 1+EPS, -EPS)));
        map.put(new FaceCorner(Direction.NORTH, RU), List.of(new Vector3f(0.5f, 1+EPS, -EPS), new Vector3f(0.5f, 0.5f, -EPS), new Vector3f(-EPS, 0.5f, -EPS), new Vector3f(-EPS, 1+EPS, -EPS)));
        map.put(new FaceCorner(Direction.NORTH, LD), List.of(new Vector3f(1+EPS, 0.5f, -EPS), new Vector3f(1+EPS, -EPS, -EPS), new Vector3f(0.5f, -EPS, -EPS), new Vector3f(0.5f, 0.5f, -EPS)));
        map.put(new FaceCorner(Direction.NORTH, RD), List.of(new Vector3f(0.5f, 0.5f, -EPS), new Vector3f(0.5f, -EPS, -EPS), new Vector3f(-EPS, -EPS, -EPS), new Vector3f(-EPS, 0.5f, -EPS)));
        map.put(new FaceCorner(Direction.UP, LU), List.of(new Vector3f(-EPS, 1+EPS, 1+EPS), new Vector3f(0.5f, 1+EPS, 1+EPS), new Vector3f(0.5f, 1+EPS, 0.5f), new Vector3f(-EPS, 1+EPS, 0.5f)));
        map.put(new FaceCorner(Direction.UP, RU), List.of(new Vector3f(-EPS, 1+EPS, 0.5f), new Vector3f(0.5f, 1+EPS, 0.5f), new Vector3f(0.5f, 1+EPS, -EPS), new Vector3f(-EPS, 1+EPS, -EPS)));
        map.put(new FaceCorner(Direction.UP, LD), List.of(new Vector3f(0.5f, 1+EPS, 1+EPS), new Vector3f(1+EPS, 1+EPS, 1+EPS), new Vector3f(1+EPS, 1+EPS, 0.5f), new Vector3f(0.5f, 1+EPS, 0.5f)));
        map.put(new FaceCorner(Direction.UP, RD), List.of(new Vector3f(0.5f, 1+EPS, 0.5f), new Vector3f(1+EPS, 1+EPS, 0.5f), new Vector3f(1+EPS, 1+EPS, -EPS), new Vector3f(0.5f, 1+EPS, -EPS)));
        map.put(new FaceCorner(Direction.DOWN, LU), List.of(new Vector3f(1+EPS, -EPS, 1+EPS), new Vector3f(0.5f, -EPS, 1+EPS), new Vector3f(0.5f, -EPS, 0.5f), new Vector3f(1+EPS, -EPS, 0.5f)));
        map.put(new FaceCorner(Direction.DOWN, RU), List.of(new Vector3f(1+EPS, -EPS, 0.5f), new Vector3f(0.5f, -EPS, 0.5f), new Vector3f(0.5f, -EPS, -EPS), new Vector3f(1+EPS, -EPS, -EPS)));
        map.put(new FaceCorner(Direction.DOWN, LD), List.of(new Vector3f(0.5f, -EPS, 1+EPS), new Vector3f(-EPS, -EPS, 1+EPS), new Vector3f(-EPS, -EPS, 0.5f), new Vector3f(0.5f, -EPS, 0.5f)));
        map.put(new FaceCorner(Direction.DOWN, RD), List.of(new Vector3f(0.5f, -EPS, 0.5f), new Vector3f(-EPS, -EPS, 0.5f), new Vector3f(-EPS, -EPS, -EPS), new Vector3f(0.5f, -EPS, -EPS)));
        return map;
    }

    private void putVertex(QuadBakingVertexConsumer builder, TextureAtlasSprite sprite, Vec3i normal, float x, float y, float z, float u, float v) {
        builder.addVertex(x, y, z);
        builder.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        builder.setNormal((float) normal.getX(), (float) normal.getY(), (float) normal.getZ());
        u = sprite.getU(u);
        v = sprite.getV(v);
        builder.setUv(u, v);
    }

    private float getU0(int index) {
        return switch (index) {
            case 1, 3 -> 0.5f;
            default -> 0;
        };
    }

    private float getU1(int index) {
        return switch (index) {
            case 1, 3 -> 1;
            default -> 0.5f;
        };
    }

    private float getV0(int index) {
        return switch (index) {
            case 2, 3 -> 0.5f;
            default -> 0;
        };
    }

    private float getV1(int index) {
        return switch (index) {
            case 2, 3 -> 1;
            default -> 0.5f;
        };
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return this.border;
    }

    @Override
    public @NotNull ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull ItemTransforms getTransforms() {
        return StandardItemTransform.get();
    }

    public static class Connect {

        private final boolean[][][] connects = new boolean[3][3][3];

        void set(int x, int y, int z) {
            this.connects[x + 1][y + 1][z + 1] = true;
        }

        int getIndex(Direction face, int corner) {
            if (getBlocked(face)) {
                return -1;
            }
            return switch (face) {
                case WEST, EAST: {
                    yield getIndexX(face, corner);
                }
                case DOWN, UP: {
                    yield getIndexY(face, corner);
                }
                case NORTH, SOUTH: {
                    yield getIndexZ(face, corner);
                }
            };
        }

        boolean getBlocked(Direction face) {
            var pos = face.getNormal().offset(1, 1, 1);
            return this.connects[pos.getX()][pos.getY()][pos.getZ()];
        }

        int getIndexX(Direction face, int corner) {
            int x = face.getStepX();
            return switch (corner) {
                case LU -> getIndex(this.connects[1][1][1+x], this.connects[1][2][1], this.connects[1][2][1+x]);
                case RU -> getIndex(this.connects[1][1][1-x], this.connects[1][2][1], this.connects[1][2][1-x]);
                case LD -> getIndex(this.connects[1][1][1+x], this.connects[1][0][1], this.connects[1][0][1+x]);
                case RD -> getIndex(this.connects[1][1][1-x], this.connects[1][0][1], this.connects[1][0][1-x]);
                default -> -1;
            };
        }

        int getIndexZ(Direction face, int corner) {
            int z = face.getStepZ();
            return switch (corner) {
                case LU -> getIndex(this.connects[1-z][1][1], this.connects[1][2][1], this.connects[1-z][2][1]);
                case RU -> getIndex(this.connects[1+z][1][1], this.connects[1][2][1], this.connects[1+z][2][1]);
                case LD -> getIndex(this.connects[1-z][1][1], this.connects[1][0][1], this.connects[1-z][0][1]);
                case RD -> getIndex(this.connects[1+z][1][1], this.connects[1][0][1], this.connects[1+z][0][1]);
                default -> -1;
            };
        }

        int getIndexY(Direction face, int corner) {
            int y = face.getStepY();
            return switch (corner) {
                case LU -> getIndex(this.connects[1][1][2], this.connects[1-y][1][1], this.connects[1-y][1][2]);
                case RU -> getIndex(this.connects[1][1][0], this.connects[1-y][1][1], this.connects[1-y][1][0]);
                case LD -> getIndex(this.connects[1][1][2], this.connects[1+y][1][1], this.connects[1+y][1][2]);
                case RD -> getIndex(this.connects[1][1][0], this.connects[1+y][1][1], this.connects[1+y][1][0]);
                default -> -1;
            };
        }

        /**
         * cbc <br>
         * axa <br>
         * cbc <br>
         */
        @SuppressWarnings("ConstantValue")
        int getIndex(boolean a, boolean b, boolean c) {
            if (!a && !b) {
                return 0;
            }
            if (a && b && !c) {
                return 1;
            }
            if (!a && b) {
                return 2;
            }
            if (a && !b) {
                return 3;
            }
            return -1;
        }

    }

    public record FaceCorner(Direction face, int corner) {

    }

}
