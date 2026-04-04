package de.pascalpex.pexnpc.commands;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.pascalpex.pexnpc.npc.NPCItemSlot;
import de.pascalpex.pexnpc.util.MessageHandler;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class NPCSlotArgument implements CustomArgumentType.Converted<NPCItemSlot, String> {
    @Override
    public @NotNull NPCItemSlot convert(@NotNull String nativeType) throws CommandSyntaxException {
        for (NPCItemSlot slot : NPCItemSlot.values()) {
            if (slot.getName().equalsIgnoreCase(nativeType)) {
                return slot;
            }
        }
        final Message exceptionMessage = MessageComponentSerializer.message().serialize(MessageHandler.errorMessage("The NPC slot you entered is invalid. Valid options are HAND, OFFHAND, HELMET, CHESTPLATE, LEGGINGS and BOOTS"));
        throw new CommandSyntaxException(new SimpleCommandExceptionType(exceptionMessage), exceptionMessage);
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        String currentInput = "";
        try {
            currentInput = context.getInput().split(" ")[3].toUpperCase();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        } // Command does not contain the argument yet
        for (NPCItemSlot slot : NPCItemSlot.values()) {
            if (slot.getName().startsWith(currentInput)) {
                builder.suggest(slot.getName());
            }
        }
        return builder.buildFuture();
    }
}
