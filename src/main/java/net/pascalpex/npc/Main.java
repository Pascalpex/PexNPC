package net.pascalpex.npc;

import me.clip.placeholderapi.PlaceholderAPI;
import net.minecraft.server.level.ServerPlayer;
import net.pascalpex.npc.events.RightClickNPC;
import net.pascalpex.npc.metrics.Metrics;
import net.pascalpex.npc.util.BungeeMessageSender;
import net.pascalpex.npc.util.NPC;
import net.pascalpex.npc.util.PacketReader;
import net.pascalpex.npc.util.TabCompletion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class Main extends JavaPlugin implements Listener {

    public String prefix = "&7[&ePexNPC&7] " + ChatColor.AQUA;

    public static final String versionUrl = "https://pascalpex.de/files/pexnpc/version.yml";

    private static Main instance;

    public static Set<String> updateNotified = new HashSet<>();
    public String newestVersion = "";

    private final int pluginId = 14923;

    private static boolean placeholdersEnabled = false;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        instance = this;

        new Metrics(this, pluginId);

        NpcData.load();
        Config.load();

        prefix = Config.getPrefix() + " " + ChatColor.AQUA;
        prefix = prefix.replace("&", "§");

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            Bukkit.getConsoleSender().sendMessage(prefix + "PlaceholderAPI konnte nicht gefunden werden!");
            Bukkit.getConsoleSender().sendMessage(prefix + "Placeholder werden nicht funktionieren");
        } else {
            placeholdersEnabled = true;
            Bukkit.getConsoleSender().sendMessage(prefix + "PlaceholderAPI wurde gefunden!");
            Bukkit.getConsoleSender().sendMessage(prefix + "Placeholder werden genutzt");
        }

        if(Config.getUpdateChecker()) {
            fetchNewestVersion();
        }

        updateNotified.clear();

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        this.getCommand("PexNPC").setTabCompleter(new TabCompletion());

        NpcData.loadNPCs();

        if(!Bukkit.getOnlinePlayers().isEmpty()) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                PacketReader reader = new PacketReader();
                try {
                    reader.inject(player);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    Bukkit.getConsoleSender().sendMessage(prefix + "Der PacketReader konnte nicht injiziert werden!");
                }
                if(NPC.getNPCs() == null) {
                    return;
                }
                if(NPC.getNPCs().isEmpty()) {
                    return;
                }
                NPC.addJoinPacket(player);
            }
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"PexNPC 1.19 von Pascalpex Aktiviert.");
    }

    private void fetchNewestVersion() {
        try
        {
            URL url = new URL(versionUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str = "";
            if ((str = in.readLine()) != null) {
                newestVersion = str.toLowerCase();
                if(!newestVersion.equals(getPlugin(this.getClass()).getDescription().getVersion())) {
                    Bukkit.getConsoleSender().sendMessage(prefix + "Eine neue Version von PexNPC ist verfügbar: " + newestVersion);
                    Bukkit.getConsoleSender().sendMessage(prefix + "Download hier: https://pascalpex.de/files/pexnpc/PexNPC.jar");
                } else {
                    Bukkit.getConsoleSender().sendMessage(prefix + "Du verwendest die neuste Version von PexNPC: " + newestVersion);
                }
            }
            in.close();
        } catch (Exception ignored) {}
    }

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            PacketReader reader = new PacketReader();
            reader.uninject(player);
            for(ServerPlayer npc : NPC.getNPCs()) {
                NPC.removeNPC(player, npc);
            }
        }
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"PexNPC 1.19 von Pascalpex Deaktiviert.");
    }

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(label.equalsIgnoreCase("pexnpc")) {
                if(player.hasPermission("pexnpc.command")) {

                    if(args.length == 0) {
                        player.sendMessage(prefix + "PexNPC 1.19 von Pascalpex");
                        player.sendMessage(prefix + "Verfügbare Befehle:");
                        player.sendMessage(prefix + "/pexnpc help " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Zeigt diese Seite an");
                        player.sendMessage(prefix + "/pexnpc reload " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Lädt die NPCs und Dateien neu");
                        player.sendMessage(prefix + "/pexnpc create [NAME] " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Erstellt einen NPC");
                        player.sendMessage(prefix + "/pexnpc list " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Zeigt alle NPCs und ihre IDs an");
                        player.sendMessage(prefix + "/pexnpc delete [ID] " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Löscht einen NPC");
                        player.sendMessage(prefix + "/pexnpc name [ID] [NAME] " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Ändert einen Namen");
                        player.sendMessage(prefix + "/pexnpc movehere [ID] " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Bewegt einen NPC zu dir");
                        player.sendMessage(prefix + "/pexnpc skin [ID] [NAME] " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Ändert den Skin");
                        player.sendMessage(prefix + "/pexnpc cmd [ID] [CMD] " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Gibt einem NPC einen Befehl");
                        player.sendMessage(prefix + "/pexnpc msg [ID] [MSG] " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Legt die Nachricht eines NPC fest");
                        player.sendMessage(prefix + "/pexnpc item [ID] [SLOT] " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Gibt einem NPC ein Item");
                        player.sendMessage(prefix + "/pexnpc clear [ID] " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Löscht die Befehle, Nachrichten und Items eines NPC");
                    }
                    if(args.length == 1) {
                        if(args[0].equalsIgnoreCase("help")) {
                            player.sendMessage(prefix + "PexNPC 1.19 von Pascalpex");
                            player.sendMessage(prefix + "Verfügbare Befehle:");
                            player.sendMessage(prefix + "/pexnpc help " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Zeigt diese Seite an");
                            player.sendMessage(prefix + "/pexnpc reload " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Lädt die NPCs und Dateien neu");
                            player.sendMessage(prefix + "/pexnpc create [NAME] " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Erstellt einen NPC");
                            player.sendMessage(prefix + "/pexnpc list " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Zeigt alle NPCs und ihre IDs an");
                            player.sendMessage(prefix + "/pexnpc delete [ID] " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Löscht einen NPC");
                            player.sendMessage(prefix + "/pexnpc name [ID] [NAME] " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Ändert einen Namen");
                            player.sendMessage(prefix + "/pexnpc movehere [ID] " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Bewegt einen NPC zu dir");
                            player.sendMessage(prefix + "/pexnpc skin [ID] [NAME] " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Ändert den Skin");
                            player.sendMessage(prefix + "/pexnpc cmd [ID] [CMD] " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Setzt den Befehl eines NPCs");
                            player.sendMessage(prefix + "/pexnpc msg [ID] [MSG] " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Legt die Nachricht eines NPC fest");
                            player.sendMessage(prefix + "/pexnpc item [ID] [SLOT] " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Gibt einem NPC ein Item");
                            player.sendMessage(prefix + "/pexnpc clear [ID] " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Löscht die Befehle, Nachrichten und Items eines NPC");
                        }
                        if(args[0].equalsIgnoreCase("reload")) {
                            Config.load();
                            NpcData.load();
                            for(Player p : Bukkit.getOnlinePlayers()) {
                                PacketReader reader = new PacketReader();
                                reader.uninject(p);
                                for(ServerPlayer npc : NPC.getNPCs()) {
                                    NPC.removeNPC(p, npc);
                                }
                            }
                            NPC.clear();
                            NpcData.loadNPCs();
                            if(!Bukkit.getOnlinePlayers().isEmpty()) {
                                for(Player p : Bukkit.getOnlinePlayers()) {
                                    PacketReader reader = new PacketReader();
                                    try {
                                        reader.inject(p);
                                    } catch (NoSuchFieldException | IllegalAccessException e) {
                                        Bukkit.getConsoleSender().sendMessage(prefix + "Der PacketReader konnte nicht injiziert werden!");
                                    }
                                    if(NPC.getNPCs() != null) {
                                        if(!NPC.getNPCs().isEmpty()) {
                                            NPC.addJoinPacket(p);
                                        }
                                    }
                                }
                            }
                            prefix = Config.getPrefix() + " " + ChatColor.AQUA;
                            prefix = prefix.replace("&", "§");
                            player.sendMessage(prefix + "Dateien und NPCs erfolgreich neu geladen");
                        }
                        if(args[0].equalsIgnoreCase("create")) {
                            player.sendMessage(prefix + ChatColor.RED + "Nutze " + ChatColor.AQUA + "/PexNPC create [NAME]");
                        }
                        if(args[0].equalsIgnoreCase("list")) {
                            player.sendMessage(prefix + "Alle geladenen NPCs:");
                            for(int i = 1; i <= NpcData.getNPCs(); i++) {
                                String name = NpcData.getName(i);
                                Location loc = NpcData.getLocation(i);
                                player.sendMessage(ChatColor.AQUA + "-" + " ID:" + i + ChatColor.GOLD + " Name: " + ChatColor.WHITE + name + ChatColor.AQUA + ChatColor.RED + " Welt: " + loc.getWorld().getName() + ChatColor.GREEN + " X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ());
                            }
                        }
                    }
                    if(args.length == 2) {
                        if(args[0].equalsIgnoreCase("create")) {
                            String name = args[1].replace("&", "§");
                            if(!checkName(name)) {
                                player.sendMessage(prefix + ChatColor.RED + "Dieser Vorname ist vergeben!");
                                return true;
                            }
                            try {
                                NPC.createNPC(player, name, name);
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    for (ServerPlayer npc : NPC.getNPCs()) {
                                        NPC.removeNPC(p, npc);
                                    }
                                }
                                NPC.clear();
                                NpcData.loadNPCs();
                                if (!Bukkit.getOnlinePlayers().isEmpty()) {
                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        if (NPC.getNPCs() != null) {
                                            if (!NPC.getNPCs().isEmpty()) {
                                                NPC.addJoinPacket(p);
                                            }
                                        }
                                    }
                                }
                            } catch (ReflectiveOperationException e) {
                                e.printStackTrace();
                            }
                            player.sendMessage(prefix + "NPC erfolgreich erstellt");
                        }
                        if(args[0].equalsIgnoreCase("delete")) {
                            String arg1 = args[1];
                            int id = 0;
                            try {
                                id = Integer.parseInt(arg1);
                            } catch (NumberFormatException e) {
                                player.sendMessage(prefix + ChatColor.RED + "Bitte gib eine ID an");
                            }
                            if(id != 0 && id <= NpcData.getNPCs()) {
                                ServerPlayer npc = NPC.getNPC(id);
                                NpcData.removeNPC(id);
                                for(Player p : Bukkit.getOnlinePlayers()) {
                                    NPC.removeNPC(p, npc);
                                }
                                player.sendMessage(prefix + "Der NPC wurde entfernt");
                            } else {
                                player.sendMessage(prefix + ChatColor.RED + "Bitte gib eine gültige ID an");
                            }
                        }
                        if(args[0].equalsIgnoreCase("movehere")) {
                            int id = 0;
                            try {
                                id = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                player.sendMessage(prefix + ChatColor.RED + "Bitte gib eine ID an");
                            }

                            if(id != 0 && id <= NpcData.getNPCs()) {
                                NpcData.moveNPC(id, player.getLocation());

                                for(Player p : Bukkit.getOnlinePlayers()) {
                                    for(ServerPlayer npc : NPC.getNPCs()) {
                                        NPC.removeNPC(p, npc);
                                    }
                                }
                                NPC.clear();
                                NpcData.loadNPCs();
                                if(!Bukkit.getOnlinePlayers().isEmpty()) {
                                    for(Player p : Bukkit.getOnlinePlayers()) {
                                        if(NPC.getNPCs() != null) {
                                            if(!NPC.getNPCs().isEmpty()) {
                                                NPC.addJoinPacket(p);
                                            }
                                        }
                                    }
                                }
                                player.sendMessage(prefix + "Der NPC wurde zu dir bewegt");
                            } else {
                                player.sendMessage(prefix + ChatColor.RED + "Bitte gib eine gültige ID an");
                            }
                        }
                        if(args[0].equalsIgnoreCase("clear")) {
                            int id = 0;
                            try {
                                id = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                player.sendMessage(prefix + ChatColor.RED + "Bitte gib eine ID an");
                            }

                            if(id != 0 && id <= NpcData.getNPCs()) {

                                NpcData.equipNPC(id, "HAND", new ItemStack(Material.AIR, 1));
                                NpcData.equipNPC(id, "OFFHAND", new ItemStack(Material.AIR, 1));
                                NpcData.equipNPC(id, "HELMET", new ItemStack(Material.AIR, 1));
                                NpcData.equipNPC(id, "CHESTPLATE", new ItemStack(Material.AIR, 1));
                                NpcData.equipNPC(id, "LEGGINGS", new ItemStack(Material.AIR, 1));
                                NpcData.equipNPC(id, "BOOTS", new ItemStack(Material.AIR, 1));

                                NpcData.setCMD(id, "UNSET");
                                NpcData.setMSG(id, "UNSET");

                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    for (ServerPlayer npc : NPC.getNPCs()) {
                                        NPC.removeNPC(p, npc);
                                    }
                                }
                                NPC.clear();
                                NpcData.loadNPCs();
                                if (!Bukkit.getOnlinePlayers().isEmpty()) {
                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        if (NPC.getNPCs() != null) {
                                            if (!NPC.getNPCs().isEmpty()) {
                                                NPC.addJoinPacket(p);
                                            }
                                        }
                                    }
                                }
                                player.sendMessage(prefix + "Der NPC wurde bereinigt von seinen Befehlen, Items und Nachrichten");
                            } else {
                                player.sendMessage(prefix + ChatColor.RED + "Bitte gib eine gültige ID an");
                            }
                        }
                    }
                    if(args.length == 3) {
                        if(args[0].equalsIgnoreCase("create")) {
                            String name = args[1].replace("&", "§");
                            if(!checkName(name)) {
                                player.sendMessage(prefix + ChatColor.RED + "Dieser Vorname ist vergeben!");
                                return true;
                            }
                            String skin = args[2];
                            try {
                                NPC.createNPC(player, name, skin);
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    for (ServerPlayer npc : NPC.getNPCs()) {
                                        NPC.removeNPC(p, npc);
                                    }
                                }
                                NPC.clear();
                                NpcData.loadNPCs();
                                if (!Bukkit.getOnlinePlayers().isEmpty()) {
                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        if (NPC.getNPCs() != null) {
                                            if (!NPC.getNPCs().isEmpty()) {
                                                NPC.addJoinPacket(p);
                                            }
                                        }
                                    }
                                }
                            } catch (ReflectiveOperationException e) {
                                e.printStackTrace();
                            }
                            player.sendMessage(prefix + "NPC erfolgreich erstellt");
                        }
                        if(args[0].equalsIgnoreCase("skin")) {
                            int id = 0;
                            try {
                                id = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                player.sendMessage(prefix + ChatColor.RED + "Bitte gib eine ID an");
                            }
                            String skin = args[2];

                            if(id != 0 && id <= NpcData.getNPCs()) {
                                NpcData.changeSkin(player, id, skin);

                                for(Player p : Bukkit.getOnlinePlayers()) {
                                    for(ServerPlayer npc : NPC.getNPCs()) {
                                        NPC.removeNPC(p, npc);
                                    }
                                }
                                NPC.clear();
                                NpcData.loadNPCs();
                                if(!Bukkit.getOnlinePlayers().isEmpty()) {
                                    for(Player p : Bukkit.getOnlinePlayers()) {
                                        if(NPC.getNPCs() != null) {
                                            if(!NPC.getNPCs().isEmpty()) {
                                                NPC.addJoinPacket(p);
                                            }
                                        }
                                    }
                                }
                                player.sendMessage(prefix + "Der Skin wurde geändert");
                            } else {
                                player.sendMessage(prefix + ChatColor.RED + "Bitte gib eine gültige ID an");
                            }
                        }
                        if(args[0].equalsIgnoreCase("item")) {
                            int id = 0;
                            try {
                                id = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                player.sendMessage(prefix + ChatColor.RED + "Bitte gib eine ID an");
                            }
                            String slot = args[2].toUpperCase();
                            ItemStack item = player.getItemInHand();
                            if(id != 0 && id <= NpcData.getNPCs()) {
                                if(slot.equals("HAND") || slot.equals("OFFHAND") || slot.equals("HELMET") || slot.equals("CHESTPLATE") || slot.equals("LEGGINGS") || slot.equals("BOOTS")) {
                                    NpcData.equipNPC(id, slot, item);
                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        for (ServerPlayer npc : NPC.getNPCs()) {
                                            NPC.removeNPC(p, npc);
                                        }
                                    }
                                    NPC.clear();
                                    NpcData.loadNPCs();
                                    if (!Bukkit.getOnlinePlayers().isEmpty()) {
                                        for (Player p : Bukkit.getOnlinePlayers()) {
                                            if (NPC.getNPCs() != null) {
                                                if (!NPC.getNPCs().isEmpty()) {
                                                    NPC.addJoinPacket(p);
                                                }
                                            }
                                        }
                                    }
                                    player.sendMessage(prefix + "Der NPC hat das Item erhalten");
                                } else {
                                    player.sendMessage(prefix + ChatColor.RED + "Gültige Slots sind: Hand, Offhand, Helmet, Chestplate, Leggings, Boots");
                                }
                            } else {
                                player.sendMessage(prefix + ChatColor.RED + "Bitte gib eine gültige ID an");
                            }
                        }
                    }
                    if(args.length >= 3) {
                        if(args[0].equalsIgnoreCase("cmd")) {
                            int id = 0;
                            try {
                                id = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                player.sendMessage(prefix + ChatColor.RED + "Bitte gib eine ID an");
                            }
                            if(id != 0 && id <= NpcData.getNPCs()) {
                                String command = "";
                                for(int i = 2; i < args.length; i++) {
                                    command = command + args[i];
                                    if(i + 1 != args.length) {
                                        command = command + " ";
                                    }
                                }
                                NpcData.setCMD(id, command);
                                for(Player p : Bukkit.getOnlinePlayers()) {
                                    for(ServerPlayer npc : NPC.getNPCs()) {
                                        NPC.removeNPC(p, npc);
                                    }
                                }
                                NPC.clear();
                                NpcData.loadNPCs();
                                if(!Bukkit.getOnlinePlayers().isEmpty()) {
                                    for(Player p : Bukkit.getOnlinePlayers()) {
                                        if(NPC.getNPCs() != null) {
                                            if(!NPC.getNPCs().isEmpty()) {
                                                NPC.addJoinPacket(p);
                                            }
                                        }
                                    }
                                }
                                player.sendMessage(prefix + "Der Befehl wurde gesetzt");
                            } else {
                                player.sendMessage(prefix + ChatColor.RED + "Bitte gib eine gültige ID an");
                            }
                        }
                        if(args[0].equalsIgnoreCase("msg")) {
                            int id = 0;
                            try {
                                id = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                player.sendMessage(prefix + ChatColor.RED + "Bitte gib eine ID an");
                            }
                            if(id != 0 && id <= NpcData.getNPCs()) {
                                String message = "";
                                for(int i = 2; i < args.length; i++) {
                                    message = message + args[i];
                                    if(i + 1 != args.length) {
                                        message = message + " ";
                                    }
                                }
                                NpcData.setMSG(id, message);
                                for(Player p : Bukkit.getOnlinePlayers()) {
                                    for(ServerPlayer npc : NPC.getNPCs()) {
                                        NPC.removeNPC(p, npc);
                                    }
                                }
                                NPC.clear();
                                NpcData.loadNPCs();
                                if(!Bukkit.getOnlinePlayers().isEmpty()) {
                                    for(Player p : Bukkit.getOnlinePlayers()) {
                                        if(NPC.getNPCs() != null) {
                                            if(!NPC.getNPCs().isEmpty()) {
                                                NPC.addJoinPacket(p);
                                            }
                                        }
                                    }
                                }
                                player.sendMessage(prefix + "Die Nachricht wurde gesetzt");
                            } else {
                                player.sendMessage(prefix + ChatColor.RED + "Bitte gib eine gültige ID an");
                            }
                        }
                        if(args[0].equalsIgnoreCase("name")) {
                            int id = 0;
                            try {
                                id = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                player.sendMessage(prefix + ChatColor.RED + "Bitte gib eine ID an");
                            }
                            if(id != 0 && id <= NpcData.getNPCs()) {
                                String name = "";
                                for(int i = 2; i < args.length; i++) {
                                    name = name + args[i];
                                    if(i + 1 != args.length) {
                                        name = name + " ";
                                    }
                                }
                                if(!checkName(name)) {
                                    player.sendMessage(prefix + ChatColor.RED + "Dieser Vorname ist vergeben!");
                                    return true;
                                }
                                name = name.replace("&", "§");
                                NpcData.changeName(id, name);
                                for(Player p : Bukkit.getOnlinePlayers()) {
                                    for(ServerPlayer npc : NPC.getNPCs()) {
                                        NPC.removeNPC(p, npc);
                                    }
                                }
                                NPC.clear();
                                NpcData.loadNPCs();
                                if(!Bukkit.getOnlinePlayers().isEmpty()) {
                                    for(Player p : Bukkit.getOnlinePlayers()) {
                                        if(NPC.getNPCs() != null) {
                                            if(!NPC.getNPCs().isEmpty()) {
                                                NPC.addJoinPacket(p);
                                            }
                                        }
                                    }
                                }
                                player.sendMessage(prefix + "Der Name wurde geändert");
                            } else {
                                player.sendMessage(prefix + ChatColor.RED + "Bitte gib eine gültige ID an");
                            }
                        }
                    }
                } else {
                    player.sendMessage(prefix + "Dafür hast du keine Rechte");
                }
            }
        } else {
            sender.sendMessage(prefix + "Dieser Befehl ist nur für Spieler geeignet");
        }
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(Config.getUpdateChecker()) {
            if(player.hasPermission("pexnpc.update")) {
                if(!updateNotified.contains(player.getUniqueId().toString())) {
                    if(newestVersion != "") {
                        if(!newestVersion.equals(getPlugin(this.getClass()).getDescription().getVersion())) {
                            player.sendMessage(prefix + "Eine neue Version von PexNPC ist verf\u00fcgbar: " + newestVersion);
                            player.sendMessage(prefix + "Download: https://pascalpex.de/files/pexnpc/PexNPC.jar");
                        }
                    }
                }
                updateNotified.add(player.getUniqueId().toString());
            }
        }
        PacketReader reader = new PacketReader();
        try {
            reader.inject(player);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Bukkit.getConsoleSender().sendMessage(prefix + "Der PacketReader konnte nicht injiziert werden!");
        }
        if(NPC.getNPCs() == null) {
            return;
        }
        if(NPC.getNPCs().isEmpty()) {
            return;
        }
        NPC.addJoinPacket(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PacketReader reader = new PacketReader();
        reader.uninject(player);
    }

    @EventHandler
    public void onNPCClick(RightClickNPC event) {
        Player player = event.getPlayer();
        int id = event.getNpc().getId();
        String msg = NPC.getMSG(id);
        if(msg != null && !msg.equalsIgnoreCase("") && !msg.equalsIgnoreCase("UNSET")) {
            if(placeholdersEnabled) {
                msg = PlaceholderAPI.setPlaceholders(player, msg);
            }
            player.sendMessage(msg.replace("&", "§"));
        }
        String cmd = NPC.getCMD(id);
        if (cmd != null && !cmd.equalsIgnoreCase("") && !cmd.equalsIgnoreCase("UNSET")) {
            if (Bukkit.getPluginCommand("server") == null && cmd.toLowerCase().startsWith("server")) {
                BungeeMessageSender bungeeMessageSender = new BungeeMessageSender();
                bungeeMessageSender.sendMessage("Connect", cmd.split(" ")[1], player);
            } else {
                Bukkit.dispatchCommand(player, cmd);
            }
            if(Config.getLogCommands()) {
                Bukkit.getConsoleSender().sendMessage(prefix + "Player " + ChatColor.GOLD + player.getName() + ChatColor.AQUA + " used NPC with ID " + ChatColor.GOLD + NPC.getID(event.getNpc()) + ChatColor.AQUA + " to dispatch command: " + ChatColor.GOLD + cmd);
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event)  {
        Player player = event.getPlayer();
        if(event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            return;
        }
        if(NPC.getNPCs() == null) {
            return;
        }
        if(NPC.getNPCs().isEmpty()) {
            return;
        }
        new BukkitRunnable() {

            @Override
            public void run() {
                NPC.addJoinPacket(player);
            }
        }.runTaskLater(this, 1);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if(NPC.getNPCs() == null) {
            return;
        }
        if(NPC.getNPCs().isEmpty()) {
            return;
        }
        new BukkitRunnable() {

            @Override
            public void run() {
                NPC.addJoinPacket(player);
            }
        }.runTaskLater(this, 1);
    }

    public boolean checkName(String name) {
        int npcSize = NpcData.getNPCs();
        String prename = name.length() > 16 ? name.substring(0, 16) : name;
        for(int i = 1; i <= npcSize; i++) {
            String currentName = NpcData.getName(i);
            if(currentName.startsWith(prename)) {
                return false;
            }
        }
        return true;
    }

}
