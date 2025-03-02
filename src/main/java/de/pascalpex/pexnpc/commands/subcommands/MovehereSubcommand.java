package de.pascalpex.pexnpc.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.files.NPCData;
import de.pascalpex.pexnpc.npc.NPCSender;
import de.pascalpex.pexnpc.npc.PlaceableNPC;
import de.pascalpex.pexnpc.util.MessageHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

public class MovehereSubcommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        Entity executor = context.getSource().getExecutor();
        PlaceableNPC placeableNPC = context.getArgument("npc", PlaceableNPC.class);

        if (executor == null) {
            sender.sendMessage(MessageHandler.errorMessage("The command executor is invalid"));
            return SINGLE_SUCCESS;
        }

        PexNPC.getPlacedNpcs().remove(placeableNPC);
        NPCSender.removeNPC(placeableNPC);

        placeableNPC.getNpc().setLocation(executor.getLocation());
        PlaceableNPC newPlaceableNPC = new PlaceableNPC(placeableNPC.getNpc());
        PexNPC.getPlacedNpcs().add(newPlaceableNPC);
        NPCSender.sendNpcToPlayers(newPlaceableNPC);
        NPCData.saveNpc(newPlaceableNPC.getNpc());

        sender.sendMessage(MessageHandler.prefixedMini("The NPC with the ID <gold>" + placeableNPC.getNpc().getId() + " <aqua>was moved"));

        return SINGLE_SUCCESS;
    }
}
