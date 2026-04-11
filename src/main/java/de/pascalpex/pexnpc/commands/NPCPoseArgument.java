package de.pascalpex.pexnpc.commands;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.pascalpex.pexnpc.npc.NPCPose;
import de.pascalpex.pexnpc.util.MessageHandler;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class NPCPoseArgument implements CustomArgumentType.Converted<NPCPose, String> {
    @Override
    public @NotNull NPCPose convert(@NotNull String nativeType) throws CommandSyntaxException {
        for (NPCPose pose : NPCPose.values()) {
            if (pose.name().equalsIgnoreCase(nativeType)) {
                return pose;
            }
        }
        final Message exceptionMessage = MessageComponentSerializer.message().serialize(MessageHandler.errorMessage("The NPC pose you entered is invalid. Valid options are STANDING, CROUCHING, SLEEPING and SWIMMING"));
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
        for (NPCPose pose : NPCPose.values()) {
            if (pose.name().startsWith(currentInput.toUpperCase())) {
                builder.suggest(pose.name());
            }
        }
        return builder.buildFuture();
    }
}
