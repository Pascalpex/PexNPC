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

public class DeleteSubcommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        PlaceableNPC placeableNPC = context.getArgument("npc", PlaceableNPC.class);

        PexNPC.getPlacedNpcs().remove(placeableNPC);
        NPCSender.removeNPC(placeableNPC);
        NPCData.deleteNpc(placeableNPC.getNpc());

        sender.sendMessage(MessageHandler.prefixedMini("The NPC with the ID <gold>" + placeableNPC.getNpc().getId() + " <aqua>got deleted"));

        return SINGLE_SUCCESS;
    }
}
