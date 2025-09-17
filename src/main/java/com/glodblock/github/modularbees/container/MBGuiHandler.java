package com.glodblock.github.modularbees.container;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.util.ContainerResolver;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceMap;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import java.util.IdentityHashMap;
import java.util.function.Supplier;

public class MBGuiHandler {

    private static final BiMap<Byte, Class<? extends ContainerResolver>> RESOLVERS = HashBiMap.create();
    private static final Byte2ReferenceMap<Supplier<? extends ContainerResolver>> RESOLVER_FACTORY = new Byte2ReferenceOpenHashMap<>();
    private static final IdentityHashMap<MenuType<?>, ContainerOpener> OPENERS = new IdentityHashMap<>();
    private static byte nextID = 0;

    public static void registerResolver(Supplier<? extends ContainerResolver> resolver) {
        var dummy = resolver.get();
        nextID ++;
        RESOLVERS.put(nextID, dummy.getClass());
        RESOLVER_FACTORY.put(nextID, resolver);
    }

    public static <T, C extends ContainerMBBase<?>> MenuTypeBuilder<T, C> builder(Class<T> hostCls) {
        return new MenuTypeBuilder<>(hostCls);
    }

    public static void open(MenuType<?> type, Player player, ContainerResolver resolver) {
        if (player.level().isClientSide()) {
            return;
        }
        var opener = OPENERS.get(type);
        if (opener != null) {
            opener.open(player, resolver);
        }
    }

    public interface ContainerConstructor<T, C extends ContainerMBBase<?>> {

        C construct(MenuType<C> type, int id, Inventory inv, T host);

    }

    public interface ContainerOpener {

        void open(Player player, ContainerResolver resolver);

    }

    public static class TileResolver implements ContainerResolver {

        private BlockPos pos;

        public TileResolver() {
            // NO-OP
        }

        public TileResolver(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public <T> T resolve(Player player, Class<T> type) {
            if (this.pos == null) {
                return null;
            }
            var te = player.level().getBlockEntity(this.pos);
            if (te == null) {
                return null;
            }
            if (type.isInstance(te)) {
                return type.cast(te);
            }
            return null;
        }

        @Override
        public void fromBytes(RegistryFriendlyByteBuf buf) {
            this.pos = BlockPos.of(buf.readLong());
        }

        @Override
        public void toBytes(RegistryFriendlyByteBuf buf) {
            buf.writeLong(this.pos.asLong());
        }

    }

    public static class MenuTypeBuilder<T, C extends ContainerMBBase<?>> {

        private final Class<T> hostCls;
        private ContainerConstructor<T, C> factory;
        private ResourceLocation id;
        private MenuType<C> type;

        private MenuTypeBuilder(Class<T> hostCls) {
            this.hostCls = hostCls;
        }

        public MenuTypeBuilder<T, C> factory(ContainerConstructor<T, C> factory) {
            this.factory = factory;
            return this;
        }

        public MenuHandler build(String id) {
            return this.build(ModularBees.id(id));
        }

        public MenuHandler build(ResourceLocation id) {
            this.id = id;
            this.type = IMenuTypeExtension.create(this::fromBytes);
            OPENERS.put(this.type, this::open);
            return new MenuHandler(this.type, this.id);
        }

        private void open(Player player, ContainerResolver resolver) {
            if (!(player instanceof ServerPlayer)) {
                return;
            }
            var host = resolver.resolve(player, this.hostCls);
            if (host == null) {
                return;
            }
            MenuProvider provider = new SimpleMenuProvider((wnd, p, pl) -> {
                var con = this.factory.construct(this.type, wnd, p, host);
                con.setResolver(resolver);
                return con;
            }, Component.empty());
            player.openMenu(provider, buffer -> toBytes(buffer, resolver));
        }

        private ContainerResolver constructResolver(RegistryFriendlyByteBuf buf) {
            var factory = RESOLVER_FACTORY.get(buf.readByte());
            if (factory == null) {
                return null;
            }
            var ins = factory.get();
            ins.fromBytes(buf);
            return ins;
        }

        private C fromBytes(int id, Inventory inv, RegistryFriendlyByteBuf buf) {
            var resolver = this.constructResolver(buf);
            var connection = Minecraft.getInstance().getConnection();
            if (resolver == null) {
                if (connection != null) {
                    connection.send(new ServerboundContainerClosePacket(id));
                }
                throw new IllegalArgumentException("Unable to look up a finder in %s. Closing the GUI.".formatted(this.id));
            }
            T host = resolver.resolve(inv.player, this.hostCls);
            if (host == null) {
                if (connection != null) {
                    connection.send(new ServerboundContainerClosePacket(id));
                }
                throw new IllegalArgumentException("Unable to find host %s in %s. Closing the GUI.".formatted(this.hostCls.getCanonicalName(), this.id));
            }
            return this.factory.construct(this.type, id, inv, host);
        }

        private void toBytes(RegistryFriendlyByteBuf buf, ContainerResolver resolver) {
            buf.writeByte(RESOLVERS.inverse().get(resolver.getClass()));
            resolver.toBytes(buf);
        }

    }

    public record MenuHandler(MenuType<?> type, ResourceLocation id) {

        public void register() {
            Registry.register(BuiltInRegistries.MENU, this.id, this.type);
        }

        @SuppressWarnings("unchecked")
        public <T extends AbstractContainerMenu> MenuType<T> castType() {
            return (MenuType<T>) this.type;
        }

    }

}
