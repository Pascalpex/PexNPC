package de.pascalpex.pexnpc.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.files.NPCData;
import de.pascalpex.pexnpc.npc.NPCSender;
import de.pascalpex.pexnpc.npc.PlaceableNPC;
import de.pascalpex.pexnpc.util.MessageHandler;
import de.pascalpex.pexnpc.util.Util;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

public class NameSubcommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        PlaceableNPC placeableNPC = context.getArgument("npc", PlaceableNPC.class);
        String name = StringArgumentType.getString(context, "name").replace("&", "§");

        if (Util.isNameInvalid(name)) {
            sender.sendMessage(MessageHandler.errorMessage("The first 16 characters of this name are already in use"));
            return SINGLE_SUCCESS;
        }

        PexNPC.getPlacedNpcs().remove(placeableNPC);
        NPCSender.removeNPC(placeableNPC);

        placeableNPC.getNpc().setName(name);
        PlaceableNPC newPlaceableNPC = new PlaceableNPC(placeableNPC.getNpc());
        PexNPC.getPlacedNpcs().add(newPlaceableNPC);
        NPCSender.sendNpcToPlayers(newPlaceableNPC);
        NPCData.saveNpc(newPlaceableNPC.getNpc());

        sender.sendMessage(MessageHandler.prefixedMini("The NPC with the ID <gold>" + placeableNPC.getNpc().getId() + " <aqua>is now called <gold>").append(MessageHandler.parseSection(name)));

        return SINGLE_SUCCESS;
    }
}
