package de.pascalpex.pexnpc.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.util.MessageHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

public class HelpSubcommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();

        sender.sendMessage(MessageHandler.prefixedMini("PexNPC " + PexNPC.getPluginVersion() + " von Pascalpex"));
        sender.sendMessage(MessageHandler.prefixedMini("Verfügbare Befehle:"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc help <dark_gray>| <gold>Zeigt diese Seite an"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc reload <dark_gray>| <gold>Lädt die NPCs und Dateien neu"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc create [NAME] <dark_gray>| <gold>Erstellt einen NPC"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc list <dark_gray>| <gold>Zeigt alle NPCs und ihre IDs an"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc delete [ID] <dark_gray>| <gold>Löscht einen NPC"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc name [ID] [NAME] <dark_gray>| <gold>Ändert einen Namen"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc movehere [ID] <dark_gray>| <gold>Bewegt einen NPC zu dir"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc skin [ID] [NAME] <dark_gray>| <gold>Ändert den Skin"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc cmd [ID] [CMD] <dark_gray>| <gold>Gibt einem NPC einen Befehl"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc msg [ID] [MSG] <dark_gray>| <gold>Legt die Nachricht eines NPC fest"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc item [ID] [SLOT] <dark_gray>| <gold>Gibt einem NPC ein Item"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc clear [ID] <dark_gray>| <gold>Löscht die Befehle, Nachrichten und Items eines NPC"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc tp [ID] <dark_gray>| <gold>Teleportiert dich zu einem NPC"));
        return SINGLE_SUCCESS;
    }
}
