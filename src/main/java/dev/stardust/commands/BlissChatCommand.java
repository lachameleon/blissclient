package dev.stardust.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.stardust.modules.BlissChat;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class BlissChatCommand extends Command {
    public BlissChatCommand() {
        super("chat", "Sends a message to Bliss chat.", "bc", "blisschat");
    }

    @Override
    public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
        builder.executes(context -> {
            BlissChat module = getModule();
            if (module == null) {
                error("BlissChat module not found.");
                return SINGLE_SUCCESS;
            }

            module.showStatus();
            return SINGLE_SUCCESS;
        });

        builder.then(argument("message", StringArgumentType.greedyString())
            .executes(context -> {
                BlissChat module = getModule();
                if (module == null) {
                    error("BlissChat module not found.");
                    return SINGLE_SUCCESS;
                }

                module.sendChat(StringArgumentType.getString(context, "message"));
                return SINGLE_SUCCESS;
            })
        );
    }

    private BlissChat getModule() {
        return Modules.get().get(BlissChat.class);
    }
}
