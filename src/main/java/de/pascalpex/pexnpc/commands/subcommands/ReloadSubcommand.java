package de.pascalpex.pexnpc.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.util.MessageHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

public class ReloadSubcommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSender sender = context.getSource().getSender();

        PexNPC.getInstance().reload();
        sender.sendMessage(MessageHandler.basicMessage("The plugin got reloaded"));
        return SINGLE_SUCCESS;
    }
}
