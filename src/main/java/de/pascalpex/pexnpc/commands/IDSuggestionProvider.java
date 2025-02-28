package de.pascalpex.pexnpc.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.npc.PlaceableNPC;
import de.pascalpex.pexnpc.util.MessageHandler;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;

import java.util.concurrent.CompletableFuture;

public class IDSuggestionProvider implements SuggestionProvider {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext context, SuggestionsBuilder builder) throws CommandSyntaxException {
        for(PlaceableNPC npc : PexNPC.getPlacedNpcs()) {
            String currentInput = "";
            try {
                currentInput = context.getArgument("id", Integer.class).toString();
            } catch (IllegalArgumentException ignored) {}
            if(String.valueOf(npc.getNpc().getId()).startsWith(currentInput)) {
                builder.suggest(String.valueOf(npc.getNpc().getId()), MessageComponentSerializer.message().serialize(MessageHandler.parseSection(npc.getNpc().getName())));
            }
        }
        return builder.buildFuture();
    }
}
