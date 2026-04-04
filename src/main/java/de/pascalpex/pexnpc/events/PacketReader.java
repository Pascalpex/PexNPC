package de.pascalpex.pexnpc.events;

import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.npc.PlaceableNPC;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class PacketReader {

    public static final Map<UUID, Channel> channels = new HashMap<>();
    public static final Map<UUID, Boolean> clicking = new HashMap<>();

    private static final String PACKET_INJECTOR_NAME = "PacketInjector";

    public void inject(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerGamePacketListenerImpl serverConnection = craftPlayer.getHandle().connection;
        Channel channel = serverConnection.connection.channel;
        channels.put(player.getUniqueId(), channel);

        if (channel.pipeline().get(PACKET_INJECTOR_NAME) != null) {
            return;
        }

        try {
            channel.pipeline().addBefore("packet_handler", PACKET_INJECTOR_NAME, new ChannelDuplexHandler() {

                @Override
                public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
                    if (msg instanceof Packet<?> packet) {
                        readPacket(player, packet);
                    }
                    super.channelRead(context, msg);
                }
            });
        } catch (NoSuchElementException ignored) {
        } // Player is no longer online
    }

    public void uninject(Player player) {
        Channel channel = channels.get(player.getUniqueId());
        if (channel == null) {
            return;
        }

        channel.eventLoop().execute(() -> {
            if (channel.pipeline().get(PACKET_INJECTOR_NAME) != null) {
                channel.pipeline().remove(PACKET_INJECTOR_NAME);
            }
        });

        channels.remove(player.getUniqueId());
    }

    public void readPacket(Player player, Packet<?> packet) {
        int id = -1;

        if (packet instanceof ServerboundInteractPacket(int entityId, _, _, _)) {
            id = entityId;
        }
        else if (packet instanceof ServerboundAttackPacket(int entityId)) {
            id = entityId;
        }

        if (id != -1) {
            PlaceableNPC placeableNPC = PexNPC.findNPCbyMinecraftID(id);
            if (placeableNPC != null) {
                ServerPlayer serverPlayer = placeableNPC.getServerPlayer();
                if (!clicking.containsKey(player.getUniqueId())) {
                    clicking.put(player.getUniqueId(), true);
                    Bukkit.getScheduler().callSyncMethod(PexNPC.getInstance(), () -> {
                        clicking.remove(player.getUniqueId());
                        Bukkit.getPluginManager().callEvent(new ClickNPCEvent(player, serverPlayer));
                        return null;
                    });
                }
            }
        }
    }
}