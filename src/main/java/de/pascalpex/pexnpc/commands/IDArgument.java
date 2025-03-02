package de.pascalpex.pexnpc.commands;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.npc.PlaceableNPC;
import de.pascalpex.pexnpc.util.MessageHandler;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class IDArgument implements CustomArgumentType.Converted<PlaceableNPC, Long> {
    @Override
    public @NotNull PlaceableNPC convert(@NotNull Long nativeType) throws CommandSyntaxException {
        PlaceableNPC placeableNPC = PexNPC.findNPCbyID(nativeType);
        if (placeableNPC != null) {
            return placeableNPC;
        }

        final Message exceptionMessage = MessageComponentSerializer.message().serialize(MessageHandler.errorMessage("An NPC with this ID does not exist"));
        throw new CommandSyntaxException(new SimpleCommandExceptionType(exceptionMessage), exceptionMessage);
    }

    @Override
    public @NotNull ArgumentType<Long> getNativeType() {
        return LongArgumentType.longArg(1);
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        String currentInput = "";
        try {
            currentInput = context.getInput().split(" ")[2];
        } catch (ArrayIndexOutOfBoundsException ignored) {
        } // Command does not contain the argument yet
        for (PlaceableNPC placeableNPC : PexNPC.getPlacedNpcs()) {
            if (String.valueOf(placeableNPC.getNpc().getId()).startsWith(currentInput)) {
                builder.suggest(String.valueOf(placeableNPC.getNpc().getId()), MessageComponentSerializer.message().serialize(MessageHandler.parseSection(placeableNPC.getNpc().getName())));
            }
        }
        return builder.buildFuture();
    }
}
