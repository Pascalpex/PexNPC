package de.pascalpex.pexnpc.events;

import de.pascalpex.pexnpc.npc.NPC;
import de.pascalpex.pexnpc.util.ReflectionHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import de.pascalpex.pexnpc.Main;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PacketReader {

    Channel channel;
    public static Map<UUID, Channel> channels = new HashMap<>();
    public static Map<UUID, Boolean> clicking = new HashMap<>();

    public void inject(Player player) throws NoSuchFieldException, IllegalAccessException {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerGamePacketListenerImpl serverConnection = craftPlayer.getHandle().connection;
        Connection connection = (Connection) ReflectionHelper.getValue(serverConnection, "e");
        channel = connection.channel;
        channels.put(player.getUniqueId(), channel);

        if (channel.pipeline().get("PacketInjector") != null) {
            return;
        }

        try {
            channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<ServerboundInteractPacket>() {

                @Override
                protected void decode(ChannelHandlerContext channel, ServerboundInteractPacket packet, List<Object> arg) {
                    arg.add(packet);
                    readPacket(player, packet);
                }

            });
        } catch (NoSuchElementException ignored) {
        } // Player is no longer online

    }

    public void uninject(Player player) {
        channel = channels.get(player.getUniqueId());
        if (channel == null) {
            return;
        }
        if (channel.pipeline().get("PacketInjector") != null) {
            channel.pipeline().remove("PacketInjector");
        }
        channels.remove(player.getUniqueId());
    }

    public void readPacket(Player player, Packet<?> packet) {

        String packetName = packet.getClass().getSimpleName();
        if (packetName.equalsIgnoreCase("PacketPlayInUseEntity") || packetName.equalsIgnoreCase("ServerboundInteractPacket")) {

            int id = (int) ReflectionHelper.getValue(packet, "b");

            for (ServerPlayer npc : Main.getNpcs().stream().map(NPC::getServerPlayer).toList()) {
                if (npc.getId() == id) {
                    if (!clicking.containsKey(player.getUniqueId())) {
                        clicking.put(player.getUniqueId(), true);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                            clicking.remove(player.getUniqueId());
                            Bukkit.getPluginManager().callEvent(new RightClickNPC(player, npc));
                        }, 1);
                    }
                }
            }

        }
    }

}
