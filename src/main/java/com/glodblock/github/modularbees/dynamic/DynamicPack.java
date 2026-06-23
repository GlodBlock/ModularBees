package com.glodblock.github.modularbees.dynamic;

import com.glodblock.github.modularbees.ModularBees;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class DynamicPack implements PackResources {

    private static final Set<String> DOMAIN = Set.of("minecraft", "c", ModularBees.MODID);
    protected final PackType type;
    protected final Map<ResourceLocation, byte[]> data = this.createMap();
    private PackLocationInfo name;

    public DynamicPack(PackType type) {
        this.type = type;
    }

    public DynamicPack build(PackLocationInfo name) {
        this.name = name;
        return this;
    }

    protected abstract Map<ResourceLocation, byte[]> createMap();

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String @NotNull ... elements) {
        return null;
    }

    public void clear(PackType side) {
        if (this.type == side) {
            this.data.clear();
        }
    }

    @Override
    public IoSupplier<InputStream> getResource(@NotNull PackType side, @NotNull ResourceLocation location) {
        if (side == this.type) {
            var byteArray = this.data.get(location);
            if (byteArray != null) {
                return () -> new ByteArrayInputStream(byteArray);
            }
        }
        return null;
    }

    @Override
    public void listResources(@NotNull PackType side, @NotNull String namespace, @NotNull String path, @NotNull ResourceOutput resourceOutput) {
        if (side == this.type) {
            if (!path.endsWith("/")) {
                path += "/";
            }
            final String finalPath = path;
            this.data.keySet().stream().filter(Objects::nonNull).filter(loc -> loc.getPath().startsWith(finalPath))
                    .forEach((id) -> {
                        var resource = this.getResource(side, id);
                        if (resource != null) {
                            resourceOutput.accept(id, resource);
                        }
                    });
        }
    }

    @Override
    public @NotNull Set<String> getNamespaces(@NotNull PackType side) {
        return this.type == side ? DOMAIN : Set.of();
    }

    @Override
    @NotNull
    public PackLocationInfo location() {
        return this.name;
    }

    @Override
    public void close() {
        // NO-OP
    }

}