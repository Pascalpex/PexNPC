package de.pascalpex.pexnpc.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.files.NPCData;
import de.pascalpex.pexnpc.npc.NPC;
import de.pascalpex.pexnpc.npc.NPCSender;
import de.pascalpex.pexnpc.npc.PlaceableNPC;
import de.pascalpex.pexnpc.util.MessageHandler;
import de.pascalpex.pexnpc.util.Util;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CreateSubcommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        Entity executor = context.getSource().getExecutor();

        if (!(executor instanceof Player target)) {
            sender.sendMessage(MessageHandler.errorMessage("The command executor must be a player"));
            return SINGLE_SUCCESS;
        }

        String name = StringArgumentType.getString(context, "name").replace("&", "§");
        if (Util.isNameInvalid(name)) {
            sender.sendMessage(MessageHandler.errorMessage("The first 16 characters of this name are already in use"));
            return SINGLE_SUCCESS;
        }

        NPC npc = new NPC(target.getLocation(), name, name, target);
        PlaceableNPC placeableNPC = new PlaceableNPC(npc);

        NPCSender.sendNpcToPlayers(placeableNPC);
        PexNPC.getPlacedNpcs().add(placeableNPC);
        NPCData.saveNpc(npc);

        sender.sendMessage(MessageHandler.prefixedMini("The NPC got created with the ID <gold>" + npc.getId()));

        return SINGLE_SUCCESS;
    }
}
