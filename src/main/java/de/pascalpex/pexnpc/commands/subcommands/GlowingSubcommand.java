package de.pascalpex.pexnpc.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.files.NPCData;
import de.pascalpex.pexnpc.npc.NPCSender;
import de.pascalpex.pexnpc.npc.PlaceableNPC;
import de.pascalpex.pexnpc.util.MessageHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

public class GlowingSubcommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        PlaceableNPC placeableNPC = context.getArgument("npc", PlaceableNPC.class);
        boolean glowing = BoolArgumentType.getBool(context, "glowing");

        PexNPC.getPlacedNpcs().remove(placeableNPC);
        NPCSender.removeNPC(placeableNPC);

        placeableNPC.getNpc().setGlowing(glowing);
        PlaceableNPC newPlaceableNPC = new PlaceableNPC(placeableNPC.getNpc());
        PexNPC.getPlacedNpcs().add(newPlaceableNPC);
        NPCSender.sendNpcToPlayers(newPlaceableNPC);
        NPCData.saveNpc(newPlaceableNPC.getNpc());

        sender.sendMessage(MessageHandler.prefixedMini("The NPC with the ID <gold>" + placeableNPC.getNpc().getId() + " <aqua>is " + (glowing ? "now" : "no longer") + " glowing"));

        return SINGLE_SUCCESS;
    }
}
