package com.glodblock.github.modularbees.dynamic;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public record MBPackSource(String name, PackType type, SimpleResourceProvider resources) implements RepositorySource {

    @Override
    public void loadPacks(Consumer<Pack> onLoad) {
        onLoad.accept(Pack.readMetaAndCreate(
                new PackLocationInfo(this.name, Component.literal(name), PackSource.BUILT_IN, Optional.empty()),
                this.resources,
                this.type,
                new PackSelectionConfig(true, Pack.Position.BOTTOM, false)
        ));
    }

    public interface SimpleResourceProvider extends Pack.ResourcesSupplier {

        default @NotNull PackResources openFull(@NotNull PackLocationInfo info, Pack.@NotNull Metadata meta) {
            return this.openPrimary(info);
        }

    }

}