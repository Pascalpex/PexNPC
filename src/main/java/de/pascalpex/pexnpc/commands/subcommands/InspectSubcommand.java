package de.pascalpex.pexnpc.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import de.pascalpex.pexnpc.events.listener.NPCClickListener;
import de.pascalpex.pexnpc.util.MessageHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Set;

public class InspectSubcommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        Entity executor = context.getSource().getExecutor();

        if (!(executor instanceof Player target)) {
            sender.sendMessage(MessageHandler.errorMessage("The command executor must be a player"));
            return SINGLE_SUCCESS;
        }

        Set<Player> inspectors = NPCClickListener.inspectors;
        if (inspectors.contains(target)) {
            inspectors.remove(target);
            sender.sendMessage(MessageHandler.prefixedMini("Inspector mode was turned <gold>off"));
        } else {
            inspectors.add(target);
            sender.sendMessage(MessageHandler.prefixedMini("Inspector mode was turned <gold>on"));
            sender.sendMessage(MessageHandler.prefixedMini("Click on any NPC to view their details"));
        }

        return SINGLE_SUCCESS;
    }
}
