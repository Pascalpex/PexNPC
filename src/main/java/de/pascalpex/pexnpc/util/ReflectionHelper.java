package de.pascalpex.pexnpc.util;

import de.pascalpex.pexnpc.PexNPC;
import net.minecraft.Optionull;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.PlayerModelPart;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;

public class ReflectionHelper {

    static Unsafe unsafe;

    static {
        try {

            Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
            singleoneInstanceField.setAccessible(true);
            unsafe = (Unsafe) singleoneInstanceField.get(null);
        } catch (Exception e) {
            PexNPC.logger().log(Level.SEVERE, "Error creating Unsafe instance");
        }
    }

    public static Object getValue(Object instance, String name) {
        if(instance == null) {
            return null;
        }

        try {
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);

            Object result = field.get(instance);

            field.setAccessible(false);
            return result;
        } catch (Exception e) {
            PexNPC.logger().log(Level.SEVERE, "Error getting field " + name + " for " + instance);
        }
        return null;
    }

    private static void setField(Object instance, String name, Object value) throws ReflectiveOperationException {
        if(instance == null) {
            return;
        }
        Field field = instance.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(instance, value);
    }

    public static ClientboundPlayerInfoUpdatePacket createInitPacket(ServerPlayer var0) {
        try {
            ClientboundPlayerInfoUpdatePacket packet = (ClientboundPlayerInfoUpdatePacket) unsafe.allocateInstance(ClientboundPlayerInfoUpdatePacket.class);
            EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions = EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER);
            setField(packet, "b", actions);
            ClientboundPlayerInfoUpdatePacket.Entry entry = new ClientboundPlayerInfoUpdatePacket.Entry(var0.getUUID(), var0.getGameProfile(), true, 0, var0.gameMode.getGameModeForPlayer(), var0.getTabListDisplayName(), var0.isModelPartShown(PlayerModelPart.HAT), 0, Optionull.map(var0.getChatSession(), RemoteChatSession::asData));
            List<ClientboundPlayerInfoUpdatePacket.Entry> entries = new ArrayList<>();
            entries.add(entry);
            setField(packet, "c", entries);
            return packet;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

}
