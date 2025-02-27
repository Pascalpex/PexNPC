package de.pascalpex.pexnpc.npc;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.datafixers.util.Pair;
import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.files.Config;
import de.pascalpex.pexnpc.util.ReflectionHelper;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
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
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(placeableNPC.getNpc().getLocation().getWorld().equals(player.getWorld())) {
                sendNPC(placeableNPC, player);
            }
        }
    }

    public static void sendNpcsToPlayer(Player player) {
        for(PlaceableNPC placeableNPC : PexNPC.getPlacedNpcs()) {
            if(placeableNPC.getNpc().getLocation().getWorld().equals(player.getWorld())) {
                sendNPC(placeableNPC, player);
            }
        }
    }

    private static void sendNPC(PlaceableNPC placeableNPC, Player player) {
        NPC npc = placeableNPC.getNpc();
        ServerPlayer serverPlayer = placeableNPC.getServerPlayer();

        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        connection.send(ReflectionHelper.createInitPacket(serverPlayer));
        // connection.send(new ClientboundEntityEventPacket(serverPlayer, (byte) 1));
        Vec3 pos = serverPlayer.position();
        connection.send(new ClientboundAddEntityPacket(serverPlayer.getId(), serverPlayer.getUUID(), pos.x(), pos.y(), pos.z(), serverPlayer.getXRot(), serverPlayer.getYRot(), serverPlayer.getType(), 0, serverPlayer.getDeltaMovement(), serverPlayer.getYHeadRot()));
        connection.send(new ClientboundRotateHeadPacket(serverPlayer, (byte) (npc.getLocation().getYaw() * 256f / 360f)));
        Scoreboard scoreboard = new Scoreboard();
        PlayerTeam team = new PlayerTeam(scoreboard, serverPlayer.getUUID().toString());
        team.setPlayerSuffix(ComponentUtils.fromMessage(new LiteralMessage(ChatColor.getLastColors(serverPlayer.getScoreboardName()) + placeableNPC.getSuffix())));
        connection.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true));
        connection.send(ClientboundSetPlayerTeamPacket.createPlayerPacket(team, serverPlayer.getScoreboardName(), ClientboundSetPlayerTeamPacket.Action.ADD));

        List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> equipmentList = new ArrayList<>();
        equipmentList.add(new Pair<>(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(npc.getHandItem())));
        equipmentList.add(new Pair<>(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(npc.getOffhandItem())));
        equipmentList.add(new Pair<>(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(npc.getHelmetItem())));
        equipmentList.add(new Pair<>(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(npc.getChestplateItem())));
        equipmentList.add(new Pair<>(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(npc.getLeggingsItem())));
        equipmentList.add(new Pair<>(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(npc.getBootsItem())));
        connection.send(new ClientboundSetEquipmentPacket(serverPlayer.getId(), equipmentList));

        new BukkitRunnable() {
            @Override
            public void run() {
                List<UUID> npcList = new ArrayList<>();
                npcList.add(serverPlayer.getUUID());
                connection.send(new ClientboundPlayerInfoRemovePacket(npcList));
            }
        }.runTaskLaterAsynchronously(PexNPC.getInstance(), Config.getSkinTimeout());

        /*
        SynchedEntityData entityData = serverPlayer.getEntityData();
        if (!entityData.isDirty()) {
            entityData.markDirty(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE));
        }
        connection.send(new ClientboundSetEntityDataPacket(serverPlayer.getId(), entityData.packDirty()));
        */
    }

    private static void removeNPC(PlaceableNPC placeableNPC, Player player) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        connection.send(new ClientboundRemoveEntitiesPacket(placeableNPC.getServerPlayer().getId()));
    }

}
