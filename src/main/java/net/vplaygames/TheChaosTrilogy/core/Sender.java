package net.vplaygames.TheChaosTrilogy.core;

import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.CheckReturnValue;

public interface Sender {
    @CheckReturnValue
    CommandReplyAction send(String message);

    @CheckReturnValue
    CommandReplyAction send(MessageEmbed embed, String placeholder);
}
