package net.pascalpex.npc.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Optionull;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
import net.pascalpex.npc.Config;
import net.pascalpex.npc.Main;
import net.pascalpex.npc.NpcData;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import sun.misc.Unsafe;
import java.util.*;

public class NPC {

    public static ItemStack nullStack = new ItemStack(Material.AIR);

    private static List<ServerPlayer> NPCs = new ArrayList<ServerPlayer>();

    public static HashMap<Integer, ServerPlayer> idMap = new HashMap<Integer, ServerPlayer>();
    public static HashMap<Integer, String> cmdMap = new HashMap<Integer, String>();
    public static HashMap<Integer, String> msgMap = new HashMap<Integer, String>();

    public static HashMap<Integer, World> worldMap = new HashMap<Integer, World>();

    public static HashMap<Integer, ItemStack> handMap = new HashMap<Integer, ItemStack>();
    public static HashMap<Integer, ItemStack> offhandMap = new HashMap<Integer, ItemStack>();
    public static HashMap<Integer, ItemStack> helmetMap = new HashMap<Integer, ItemStack>();
    public static HashMap<Integer, ItemStack> chestplateMap = new HashMap<Integer, ItemStack>();
    public static HashMap<Integer, ItemStack> leggingsMap = new HashMap<Integer, ItemStack>();
    public static HashMap<Integer, ItemStack> bootsMap = new HashMap<Integer, ItemStack>();

    static Unsafe unsafe;
    static {
        try {

            Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
            singleoneInstanceField.setAccessible(true);
            unsafe = (Unsafe) singleoneInstanceField.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setField(Object instance, String name, Object value) throws ReflectiveOperationException{
        Validate.notNull(instance);
        Field field = instance.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(instance, value);
    }

    public static void createNPC(Player player, String name, String skin) throws ReflectiveOperationException {
        Location loc = player.getLocation();
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel world = ((CraftWorld)loc.getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);
        ServerPlayer npc = new ServerPlayer(server, world, gameProfile, ClientInformation.createDefault());
        npc.setPos(loc.getX(), loc.getY(), loc.getZ());
        npc.setYRot(loc.getYaw());
        npc.setXRot(loc.getPitch());
        SynchedEntityData watcher = npc.getEntityData();
        setField(watcher, "registrationLocked", false);
        int skinMode = Config.getSkinMode();
        switch (skinMode) {
            case 2:
                watcher.set(new EntityDataAccessor<Byte>(17, EntityDataSerializers.BYTE), (byte)126);
                break;
            case 3:
                watcher.set(new EntityDataAccessor<Byte>(17, EntityDataSerializers.BYTE), (byte)127);
                break;
            default:
                break;
        }

        String[] nameS = getSkin(player, skin);
        gameProfile.getProperties().put("textures", new Property("textures", nameS[0], nameS[1]));

        handMap.put(npc.getId(), nullStack);
        offhandMap.put(npc.getId(), nullStack);
        helmetMap.put(npc.getId(), nullStack);
        chestplateMap.put(npc.getId(), nullStack);
        leggingsMap.put(npc.getId(), nullStack);
        bootsMap.put(npc.getId(), nullStack);

        addNPCPacket(npc);
        NPCs.add(npc);

        int id = 1;
        if(NpcData.config.contains("npcs")) {
            id = NpcData.config.getConfigurationSection("npcs").getKeys(false).size() + 1;
        }
        worldMap.put(npc.getId(), loc.getWorld());
        idMap.put(id, npc);
        cmdMap.put(npc.getId(), "");
        msgMap.put(npc.getId(), "");

        NpcData.saveNPC(loc, name, nameS, id);
    }

    public static void loadNPC(Location loc, GameProfile profile, int id, String cmd, String msg, ItemStack handItem, ItemStack offhandItem, ItemStack helmetItem, ItemStack chestplateItem, ItemStack leggingsItem, ItemStack bootsItem) throws ReflectiveOperationException {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel world = ((CraftWorld)loc.getWorld()).getHandle();
        ServerPlayer npc = new ServerPlayer(server, world, profile, ClientInformation.createDefault());
        npc.setPos(loc.getX(), loc.getY(), loc.getZ());
        npc.setYRot(loc.getYaw());
        npc.setXRot(loc.getPitch());

        SynchedEntityData watcher = npc.getEntityData();
        setField(watcher, "registrationLocked", false);
        int skinMode = Config.getSkinMode();
        switch (skinMode) {
            case 2:
                watcher.set(new EntityDataAccessor<Byte>(17, EntityDataSerializers.BYTE), (byte)126);
                break;
            case 3:
                watcher.set(new EntityDataAccessor<Byte>(17, EntityDataSerializers.BYTE), (byte)127);
                break;
            default:
                break;
        }

        idMap.put(id, npc);
        cmdMap.put(npc.getId(), cmd);
        msgMap.put(npc.getId(), msg);

        worldMap.put(npc.getId(), loc.getWorld());

        handMap.put(npc.getId(), handItem);
        offhandMap.put(npc.getId(), offhandItem);
        helmetMap.put(npc.getId(), helmetItem);
        chestplateMap.put(npc.getId(), chestplateItem);
        leggingsMap.put(npc.getId(), leggingsItem);
        bootsMap.put(npc.getId(), bootsItem);

        addNPCPacket(npc);
        NPCs.add(npc);
    }

    public static ServerPlayer getNPC(int id) {
        return idMap.get(id);
    }

    public static int getID(ServerPlayer npc) {
        for(int id : idMap.keySet()) {
            if(idMap.get(id).equals(npc)) {
                return id;
            }
        }
        return -1;
    }

    public static String getCMD(int id) {
        if(!cmdMap.containsKey(id)) {
            return "";
        }
        return cmdMap.get(id);
    }

    public static String getMSG(int id) {
        if(!msgMap.containsKey(id)) {
            return "";
        }
        return msgMap.get(id);
    }

    public static String[] getSkin(Player player, String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

            URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader2 = new InputStreamReader(url2.openStream());
            JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String texture = property.get("value").getAsString();
            String signature = property.get("signature").getAsString();
            return new String[] {texture, signature};

        } catch (Exception e) {
            ServerPlayer p = ((CraftPlayer)player).getHandle();
            GameProfile profile = p.getGameProfile();
            Property property = profile.getProperties().get("textures").iterator().next();
            String texture = property.value();
            String signature = property.signature();
            return new String[] {texture, signature};
        }
    }

    public static void addNPCPacket(ServerPlayer npc) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(worldMap.get(npc.getId()))) {
                ServerGamePacketListenerImpl connection = ((CraftPlayer)player).getHandle().connection;
                connection.send(createInitPacket(npc));
                connection.send(new ClientboundAddEntityPacket(npc));
                connection.send(new ClientboundRotateHeadPacket(npc, (byte) (NpcData.getLocation(getID(npc)).getYaw() * 256f / 360f)));

                List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> equipmentList = new ArrayList<>();
                equipmentList.add(new Pair<>(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(handMap.get(npc.getId()))));
                equipmentList.add(new Pair<>(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(offhandMap.get(npc.getId()))));
                equipmentList.add(new Pair<>(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(helmetMap.get(npc.getId()))));
                equipmentList.add(new Pair<>(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(chestplateMap.get(npc.getId()))));
                equipmentList.add(new Pair<>(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(leggingsMap.get(npc.getId()))));
                equipmentList.add(new Pair<>(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(bootsMap.get(npc.getId()))));
                connection.send(new ClientboundSetEquipmentPacket(npc.getId(), equipmentList));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        List<UUID> npcList = new ArrayList<>();
                        npcList.add(npc.getUUID());
                        connection.send(new ClientboundPlayerInfoRemovePacket(npcList));
                    }
                }.runTaskLaterAsynchronously(Main.getInstance(), Config.getSkinTimeout());
            }
        }
    }

    public static void addJoinPacket(Player player) {
        for(ServerPlayer npc : NPCs) {
            if (player.getWorld().equals(worldMap.get(npc.getId()))) {
                ServerGamePacketListenerImpl connection = ((CraftPlayer)player).getHandle().connection;
                connection.send(createInitPacket(npc));
                connection.send(new ClientboundEntityEventPacket(npc, (byte) 1));
                connection.send(new ClientboundAddEntityPacket(npc));
                connection.send(new ClientboundRotateHeadPacket(npc, (byte) (NpcData.getLocation(getID(npc)).getYaw() * 256f / 360f)));

                List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> equipmentList = new ArrayList<>();
                equipmentList.add(new Pair<>(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(handMap.get(npc.getId()))));
                equipmentList.add(new Pair<>(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(offhandMap.get(npc.getId()))));
                equipmentList.add(new Pair<>(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(helmetMap.get(npc.getId()))));
                equipmentList.add(new Pair<>(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(chestplateMap.get(npc.getId()))));
                equipmentList.add(new Pair<>(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(leggingsMap.get(npc.getId()))));
                equipmentList.add(new Pair<>(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(bootsMap.get(npc.getId()))));
                connection.send(new ClientboundSetEquipmentPacket(npc.getId(), equipmentList));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        List<UUID> npcList = new ArrayList<>();
                        npcList.add(npc.getUUID());
                        connection.send(new ClientboundPlayerInfoRemovePacket(npcList));
                    }
                }.runTaskLaterAsynchronously(Main.getInstance(), Config.getSkinTimeout());

                SynchedEntityData entityData = npc.getEntityData();
                if (!entityData.isDirty()) {
                    entityData.markDirty(new EntityDataAccessor<Byte>(17, EntityDataSerializers.BYTE));
                }
                connection.send(new ClientboundSetEntityDataPacket(npc.getId(), entityData.packDirty()));
            }

        }
    }

    public static List<ServerPlayer> getNPCs() {
        return NPCs;
    }

    public static void removeNPC(Player player, ServerPlayer npc) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer)player).getHandle().connection;
        connection.send(new ClientboundRemoveEntitiesPacket(npc.getId()));
    }

    public static void clear() {
        NPCs.clear();
        idMap.clear();
        cmdMap.clear();
        msgMap.clear();
        worldMap.clear();

        handMap.clear();
        offhandMap.clear();
        helmetMap.clear();
        chestplateMap.clear();
        leggingsMap.clear();
        bootsMap.clear();
    }

    /**
     * Constructs an initiation packet for the given npc using unsafe and reflection because the public constructors of the ClientboundPlayerInfoUpdatePacket class are not suitable
     * @param var0 npc to create the packet for
     * @return initiation packet
     */
    private static ClientboundPlayerInfoUpdatePacket createInitPacket(ServerPlayer var0) {
        try {
            ClientboundPlayerInfoUpdatePacket packet = (ClientboundPlayerInfoUpdatePacket) unsafe.allocateInstance(ClientboundPlayerInfoUpdatePacket.class);
            EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions = EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER);
            setField(packet, "a", actions);
            ClientboundPlayerInfoUpdatePacket.Entry entry = new ClientboundPlayerInfoUpdatePacket.Entry(var0.getUUID(), var0.getGameProfile(), true, 0, var0.gameMode.getGameModeForPlayer(), var0.getTabListDisplayName(), (RemoteChatSession.Data) Optionull.map(var0.getChatSession(), RemoteChatSession::asData));
            List<ClientboundPlayerInfoUpdatePacket.Entry> entries = new ArrayList<>();
            entries.add(entry);
            setField(packet, "b", entries);
            return packet;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
