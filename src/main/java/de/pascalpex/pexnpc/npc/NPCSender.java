package de.pascalpex.pexnpc.npc;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.datafixers.util.Pair;
import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.files.Config;
import net.minecraft.Optionull;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class NPCSender {

    public static void resendEverything() {
        removeEverything();
        sendEverything();
    }

    public static void removeEverything() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            for(PlaceableNPC placeableNPC : PexNPC.getPlacedNpcs()) {
                removeNPC(placeableNPC, player);
            }
        }
    }

    public static void sendEverything() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            sendNpcsToPlayer(player);
        }
    }

    public static void sendNpcToPlayers(PlaceableNPC placeableNPC) {
        List<Packet<?>> packets = buildNPCPackets(placeableNPC);
        for(Player player : Bukkit.getOnlinePlayers()) {
            ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
            if(placeableNPC.getNpc().getLocation().getWorld().equals(player.getWorld())) {
                sendNPC(placeableNPC, packets, connection);
            }
        }
    }

    public static void sendNpcsToPlayer(Player player) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        for(PlaceableNPC placeableNPC : PexNPC.getPlacedNpcs()) {
            List<Packet<?>> packets = buildNPCPackets(placeableNPC);
            if(placeableNPC.getNpc().getLocation().getWorld().equals(player.getWorld())) {
                sendNPC(placeableNPC, packets, connection);
            }
        }
    }

    private static void sendNPC(PlaceableNPC placeableNPC, List<Packet<?>> packets, ServerGamePacketListenerImpl connection) {
        for(Packet<?> packet : packets) {
            connection.send(packet);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                List<UUID> npcList = new ArrayList<>();
                npcList.add(placeableNPC.getServerPlayer().getUUID());
                connection.send(new ClientboundPlayerInfoRemovePacket(npcList));
            }
        }.runTaskLaterAsynchronously(PexNPC.getInstance(), Config.getSkinTimeout());
    }

    private static List<Packet<?>> buildNPCPackets(PlaceableNPC placeableNPC) {
        NPC npc = placeableNPC.getNpc();
        ServerPlayer serverPlayer = placeableNPC.getServerPlayer();
        List<Packet<?>> packets = new ArrayList<>();

        packets.add(new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER), new ClientboundPlayerInfoUpdatePacket.Entry(serverPlayer.getUUID(), serverPlayer.getGameProfile(), true, 0, serverPlayer.gameMode.getGameModeForPlayer(), serverPlayer.getTabListDisplayName(), serverPlayer.isModelPartShown(PlayerModelPart.HAT), 0, Optionull.map(serverPlayer.getChatSession(), RemoteChatSession::asData))));
        Vec3 pos = serverPlayer.position();
        packets.add(new ClientboundAddEntityPacket(serverPlayer.getId(), serverPlayer.getUUID(), pos.x(), pos.y(), pos.z(), serverPlayer.getXRot(), serverPlayer.getYRot(), serverPlayer.getType(), 0, serverPlayer.getDeltaMovement(), serverPlayer.getYHeadRot()));
        packets.add(new ClientboundRotateHeadPacket(serverPlayer, (byte) (npc.getLocation().getYaw() * 256f / 360f)));
        Scoreboard scoreboard = new Scoreboard();
        PlayerTeam team = new PlayerTeam(scoreboard, serverPlayer.getUUID().toString());
        team.setPlayerSuffix(ComponentUtils.fromMessage(new LiteralMessage(ChatColor.getLastColors(serverPlayer.getScoreboardName()) + placeableNPC.getSuffix())));
        packets.add(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true));
        packets.add(ClientboundSetPlayerTeamPacket.createPlayerPacket(team, serverPlayer.getScoreboardName(), ClientboundSetPlayerTeamPacket.Action.ADD));

        List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> equipmentList = new ArrayList<>();
        equipmentList.add(new Pair<>(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(npc.getHandItem())));
        equipmentList.add(new Pair<>(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(npc.getOffhandItem())));
        equipmentList.add(new Pair<>(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(npc.getHelmetItem())));
        equipmentList.add(new Pair<>(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(npc.getChestplateItem())));
        equipmentList.add(new Pair<>(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(npc.getLeggingsItem())));
        equipmentList.add(new Pair<>(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(npc.getBootsItem())));
        packets.add(new ClientboundSetEquipmentPacket(serverPlayer.getId(), equipmentList));

        return packets;
    }

    private static void removeNPC(PlaceableNPC placeableNPC, Player player) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        connection.send(new ClientboundRemoveEntitiesPacket(placeableNPC.getServerPlayer().getId()));
    }

}
