package net.pascalpex.npc.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.pascalpex.npc.Main;
import net.pascalpex.npc.events.RightClickNPC;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;

public class PacketReader {

    Channel channel;
    public static Map<UUID, Channel> channels = new HashMap<UUID, Channel>();
    public static Map<UUID, Boolean> clicking = new HashMap<UUID, Boolean>();

    public void inject(Player player) throws NoSuchFieldException, IllegalAccessException {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerGamePacketListenerImpl serverConnection = craftPlayer.getHandle().connection;
        Field connectionField = ServerCommonPacketListenerImpl.class.getDeclaredField("c");
        connectionField.setAccessible(true);
        Connection connection = (Connection) connectionField.get(serverConnection);
        channel = connection.channel;
        channels.put(player.getUniqueId(), channel);

        if(channel.pipeline().get("PacketInjector") != null) {
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
        } catch (NoSuchElementException ignored) {} // Player is no longer online

    }

    public void uninject(Player player) {
        channel = channels.get(player.getUniqueId());
        if(channel == null) {
            return;
        }
        if(channel.pipeline().get("PacketInjector") != null) {
            channel.pipeline().remove("PacketInjector");
        }
        channels.remove(player.getUniqueId());
    }

    public void readPacket(Player player, Packet<?> packet) {

        if(packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {

            int id = (int) getValue(packet, "a");

                for(ServerPlayer npc : NPC.getNPCs()) {
                    if(npc.getId() == id) {
                        if (!clicking.containsKey(player.getUniqueId())) {
                            clicking.put(player.getUniqueId(), true);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
                                @Override
                                public void run() {
                                    clicking.remove(player.getUniqueId());
                                    Bukkit.getPluginManager().callEvent(new RightClickNPC(player, npc));
                                }
                            }, 1);
                        }
                    }
                }

        }
    }

    private Object getValue(Object instance, String name) {

        Object result = "nothingYet";

        try {

            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);

            result = field.get(instance);

            field.setAccessible(false);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
