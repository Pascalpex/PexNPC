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
        sender.sendMessage(MessageHandler.prefixedMini("Available commands:"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc help <dark_gray>| <gold>Shows this page"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc reload <dark_gray>| <gold>Reloads the files and NPCs"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc create [NAME] <dark_gray>| <gold>Creates a new NPC"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc list <dark_gray>| <gold>Shows all NPCs and their IDs"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc delete [ID] <dark_gray>| <gold>Deletes a NPC"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc name [ID] [NAME] <dark_gray>| <gold>Changes a name"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc movehere [ID] <dark_gray>| <gold>Moves a NPC to you"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc skin [ID] [NAME] <dark_gray>| <gold>Changes a skin"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc cmd [ID] [CMD] <dark_gray>| <gold>Gives a command to a NPC"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc msg [ID] [MSG] <dark_gray>| <gold>Gives a message to a NPC"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc item [ID] [SLOT] <dark_gray>| <gold>Gives an item to a NPC"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc clear [ID] <dark_gray>| <gold>Clears the command, message and items of a NPC"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc tp [ID] <dark_gray>| <gold>Teleports to a NPC"));
        return SINGLE_SUCCESS;
    }
}
