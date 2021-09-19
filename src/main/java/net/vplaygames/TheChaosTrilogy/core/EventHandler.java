/*
 * Copyright 2020-2021 Vaibhav Nargwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.vplaygames.TheChaosTrilogy.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.vplaygames.TheChaosTrilogy.commands.AbstractBotCommand;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.regex.Pattern;

public class EventHandler extends ListenerAdapter {
    private static EventHandler instance;
    private static Pattern selfMention;

    protected EventHandler() {
    }

    public static EventHandler getInstance() {
        return instance == null ? instance = new EventHandler() : instance;
    }

    public static Pattern getSelfMentionPattern() {
        return selfMention == null ? selfMention = Pattern.compile("<@!?>") : selfMention;
    }

    public static void botPingedEvent(MessageReceivedEvent e) {
        e.getChannel()
            .sendMessage("Prefix: " + Bot.PREFIX)
            .setEmbeds(new EmbedBuilder()
                .setAuthor("V Play Games Bot Info")
                .setDescription("A Pokemon-related discord bot created, developed & maintained by")
                .appendDescription(" V Play Games aka VPG (<@" + Bot.BOT_OWNER + ">)")
                .addField("Developer", "<@" + Bot.BOT_OWNER + ">", true)
                .addField("Version", Bot.VERSION, true)
                .addField("Server Count", String.valueOf(e.getJDA().getGuilds().size()), false)
                .setTimestamp(Instant.now())
                .setThumbnail(e.getJDA().getSelfUser().getAvatarUrl())
                .setColor(0x1abc9c)
                .build())
            .queue();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        try {
            if (Bot.closed) {
                if (e.getMessage().getContentRaw().equalsIgnoreCase("v!activate")
                    && e.getAuthor().getIdLong() == Bot.BOT_OWNER) {
                    Bot.closed = false;
                    e.getChannel().sendMessage("Thanks for activating me again!").queue();
                }
            } else if (!e.getAuthor().isBot() && (!e.isFromGuild() || ((TextChannel) e.getChannel()).canTalk())) {
                Message message = e.getMessage();
                String content = message.getContentRaw();
                String[] args = content.split("[\n\\s]+");
                if (content.toLowerCase().startsWith(Bot.PREFIX)) {
                    AbstractBotCommand command = Bot.commands.get(args[0].substring(Bot.PREFIX.length()).toLowerCase());
                    if (command != null)
                        CommandReceivedEvent.run(e, args, command);
                } else if (Util.equalsAnyIgnoreCase(Util.reduceToAlphabets(content), "Hi", "Hey", "Hello", "Bye"))
                    e.getChannel().sendMessage(Util.toProperCase(content) + "!").queue();
                else if (getSelfMentionPattern().matcher(message.getContentRaw()).find())
                    botPingedEvent(e);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent e) {
        try {
            if (Bot.closed) {
                if (e.getName().equalsIgnoreCase("activate")
                    && e.getUser().getIdLong() == Bot.BOT_OWNER) {
                    Bot.closed = false;
                    e.getChannel().sendMessage("Thanks for activating me again!").queue();
                }
            } else {
                AbstractBotCommand command = Bot.commands.get(e.getName());
                if (command != null) {
                    CommandReceivedEvent.run(e, command);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void onException(@Nonnull ExceptionEvent e) {
        e.getCause().printStackTrace();
    }

    @Override
    public void onReady(@Nonnull ReadyEvent e) {
        try {
            Bot.init();
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public void onButtonClick(@Nonnull ButtonClickEvent e) {
        String method = Util.getMethod(e.getComponentId());
        String[] args = Util.getArgs(e.getComponentId()).split(":");
        ButtonHandler handler = Bot.buttonHandlers.get(method);
        if (handler.isValidClick(e, args)) {
            handler.handle(e, args);
        }
    }
}
