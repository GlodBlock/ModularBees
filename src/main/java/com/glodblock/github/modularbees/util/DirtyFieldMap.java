package com.glodblock.github.modularbees.util;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.network.SMBFieldAutoUpdate;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceMap;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DirtyFieldMap {

    private final Short2ReferenceMap<RemoteField<?>> fields = new Short2ReferenceOpenHashMap<>();

    public boolean isEmpty() {
        return this.fields.isEmpty();
    }

    public boolean needSync() {
        if (this.isEmpty()) {
            return false;
        }
        for (var field : this.fields.values()) {
            if (field.needSync()) {
                return true;
            }
        }
        return false;
    }

    public <X> DirtyFieldMap add(int intID, Supplier<X> getter, Consumer<X> setter, StreamCodec<? super RegistryFriendlyByteBuf, X> codec) {
        short id = (short) intID;
        if (id <= 0) {
            throw new IllegalArgumentException("Find invalid id: %s.".formatted(id));
        }
        if (this.fields.containsKey(id)) {
            throw new IllegalArgumentException("Find duplicate id: %s.".formatted(id));
        }
        this.fields.put(id, new RemoteField<>(getter, setter, codec));
        return this;
    }

    public SMBFieldAutoUpdate sendFullPacket(int id) {
        return new SMBFieldAutoUpdate(buf -> this.fieldUpdate(buf, true), id);
    }

    public SMBFieldAutoUpdate sendDeltaPacket(int id) {
        return new SMBFieldAutoUpdate(buf -> this.fieldUpdate(buf, false), id);
    }

    public void fieldSync(RegistryFriendlyByteBuf buf) {
        while (true) {
            var id = buf.readShort();
            if (id <= 0) {
                break;
            }
            var field = this.fields.get(id);
            if (field == null) {
                ModularBees.LOGGER.warn("Find invalid sync id: %s".formatted(id));
                break;
            }
            field.sync(buf);
        }
    }

    public void fieldUpdate(RegistryFriendlyByteBuf buf, boolean fullUpdate) {
        for (var e : this.fields.short2ReferenceEntrySet()) {
            var field = e.getValue();
            if (!fullUpdate && !field.needSync()) {
                continue;
            }
            buf.writeShort(e.getShortKey());
            field.sendUpdate(buf);
        }
        buf.writeShort(-1);
    }

    // Basic types
    public DirtyFieldMap addInt(int id, Supplier<Integer> getter, Consumer<Integer> setter) {
        return this.add(id, getter, setter, ByteBufCodecs.INT);
    }

    public DirtyFieldMap addDouble(int id, Supplier<Double> getter, Consumer<Double> setter) {
        return this.add(id, getter, setter, ByteBufCodecs.DOUBLE);
    }

    public DirtyFieldMap addBool(int id, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return this.add(id, getter, setter, ByteBufCodecs.BOOL);
    }

    public DirtyFieldMap addString(int id, Supplier<String> getter, Consumer<String> setter) {
        return this.add(id, getter, setter, ByteBufCodecs.STRING_UTF8);
    }

    public <E extends Enum<E>> DirtyFieldMap addEnum(int id, Class<E> type, Supplier<E> getter, Consumer<E> setter) {
        return this.add(id, getter, setter, EnumStreamCodec.of(type));
    }

    // Minecraft types
    public DirtyFieldMap addItem(int id, Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
        return this.add(id, getter, setter, ItemStack.OPTIONAL_STREAM_CODEC);
    }

    public DirtyFieldMap addFluid(int id, Supplier<FluidStack> getter, Consumer<FluidStack> setter) {
        return this.add(id, getter, setter, FluidStack.OPTIONAL_STREAM_CODEC);
    }

    private static class RemoteField<X> {

        private X remoteValue;
        private final Supplier<X> getter;
        private final Consumer<X> setter;
        private final StreamCodec<? super RegistryFriendlyByteBuf, X> codec;

        RemoteField(Supplier<X> getter, Consumer<X> setter, StreamCodec<? super RegistryFriendlyByteBuf, X> codec) {
            this.getter = getter;
            this.setter = setter;
            this.codec = codec;
        }

        boolean needSync() {
            return !Objects.equals(this.getter.get(), this.remoteValue);
        }

        void sync(RegistryFriendlyByteBuf buf) {
            this.remoteValue = this.codec.decode(buf);
            this.setter.accept(this.remoteValue);
        }

        void sendUpdate(RegistryFriendlyByteBuf buf) {
            this.codec.encode(buf, this.getter.get());
        }

    }

    private static class EnumStreamCodec<E extends Enum<E>> implements StreamCodec<ByteBuf, E> {

        private E[] values;
        private static final IdentityHashMap<Class<?>, EnumStreamCodec<? extends Enum<?>>> CACHE = new IdentityHashMap<>();

        @SuppressWarnings("unchecked")
        static <E extends Enum<E>> EnumStreamCodec<E> of(Class<E> clazz) {
            if (CACHE.containsKey(clazz)) {
                return (EnumStreamCodec<E>) CACHE.get(clazz);
            }
            EnumStreamCodec<E> codec = new EnumStreamCodec<>();
            codec.values = clazz.getEnumConstants();
            CACHE.put(clazz, codec);
            return codec;
        }

        @Override
        public @NotNull E decode(@NotNull ByteBuf buf) {
            return this.values[buf.readShort()];
        }

        @Override
        public void encode(@NotNull ByteBuf buf, @NotNull E value) {
            buf.writeShort(value.ordinal());
        }

    }

}
