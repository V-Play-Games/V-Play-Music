package net.vplaygames.vpm.core;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.Interaction;

import javax.annotation.CheckReturnValue;
import java.util.function.Supplier;

public interface Sender {
    @CheckReturnValue
    CommandReplyAction send(String message);

    @CheckReturnValue
    CommandReplyAction send(MessageEmbed embed, String placeholder);

    static Sender fromInteraction(Interaction interaction) {
        return fromSupplier(() -> new CommandReplyAction(interaction));
    }

    static Sender fromMessage(Message message)  {
        return fromSupplier(() -> new CommandReplyAction(message));
    }

    static Sender fromChannel(TextChannel tc) {
        return fromSupplier(() -> new CommandReplyAction(tc));
    }

    static Sender fromSupplier(Supplier<CommandReplyAction> supplier) {
        return new Sender() {
            @Override
            public CommandReplyAction send(String message) {
                return supplier.get().setContent(message);
            }

            @Override
            public CommandReplyAction send(MessageEmbed embed, String placeholder) {
                return supplier.get().addEmbeds(embed);
            }
        };
    }
}
