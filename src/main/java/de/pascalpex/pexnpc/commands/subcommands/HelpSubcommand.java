package de.pascalpex.pexnpc.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.util.MessageHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;

public class HelpSubcommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        HoverEvent<Component> hoverEvent = HoverEvent.showText(MessageHandler.parse("<aqua>Click to execute"));

        sender.sendMessage(MessageHandler.prefixedMini("PexNPC " + PexNPC.getPluginVersion() + " by Pascalpex"));
        sender.sendMessage(MessageHandler.prefixedMini("Available commands:"));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc help <dark_gray>| <gold>Shows this page").hoverEvent(hoverEvent).clickEvent(ClickEvent.suggestCommand("/pexnpc help")));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc reload <dark_gray>| <gold>Reloads the files and NPCs").hoverEvent(hoverEvent).clickEvent(ClickEvent.suggestCommand("/pexnpc reload")));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc create [NAME] <dark_gray>| <gold>Creates a new NPC").hoverEvent(hoverEvent).clickEvent(ClickEvent.suggestCommand("/pexnpc create")));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc list <dark_gray>| <gold>Shows all NPCs and their IDs").hoverEvent(hoverEvent).clickEvent(ClickEvent.suggestCommand("/pexnpc list")));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc delete [ID] <dark_gray>| <gold>Deletes a NPC").hoverEvent(hoverEvent).clickEvent(ClickEvent.suggestCommand("/pexnpc delete")));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc name [ID] [NAME] <dark_gray>| <gold>Changes a name").hoverEvent(hoverEvent).clickEvent(ClickEvent.suggestCommand("/pexnpc name")));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc movehere [ID] <dark_gray>| <gold>Moves a NPC to you").hoverEvent(hoverEvent).clickEvent(ClickEvent.suggestCommand("/pexnpc movehere")));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc skin [ID] [NAME] <dark_gray>| <gold>Changes a skin").hoverEvent(hoverEvent).clickEvent(ClickEvent.suggestCommand("/pexnpc skin")));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc cmd [ID] [CMD] <dark_gray>| <gold>Gives a command to a NPC").hoverEvent(hoverEvent).clickEvent(ClickEvent.suggestCommand("/pexnpc cmd")));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc msg [ID] [MSG] <dark_gray>| <gold>Gives a message to a NPC").hoverEvent(hoverEvent).clickEvent(ClickEvent.suggestCommand("/pexnpc msg")));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc item [ID] [SLOT] <dark_gray>| <gold>Gives an item to a NPC").hoverEvent(hoverEvent).clickEvent(ClickEvent.suggestCommand("/pexnpc item")));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc clear [ID] <dark_gray>| <gold>Clears the command, message and items of a NPC").hoverEvent(hoverEvent).clickEvent(ClickEvent.suggestCommand("/pexnpc clear")));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc tp [ID] <dark_gray>| <gold>Teleports to a NPC").hoverEvent(hoverEvent).clickEvent(ClickEvent.suggestCommand("/pexnpc tp")));
        sender.sendMessage(MessageHandler.prefixedMini("/pexnpc inspect <dark_gray>| <gold>Toggles the inspection mode").hoverEvent(hoverEvent).clickEvent(ClickEvent.suggestCommand("/pexnpc inspect")));
        return SINGLE_SUCCESS;
    }
}
