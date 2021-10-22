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
package net.vplaygames.vpm.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.vplaygames.vpm.commands.AbstractBotCommand;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class EventHandler extends ListenerAdapter {
    private static EventHandler instance;
    private static Pattern selfMention;
    private AtomicInteger shardsInit = new AtomicInteger();

    protected EventHandler() {
    }

    public static EventHandler getInstance() {
        return instance == null ? instance = new EventHandler() : instance;
    }

    public static Pattern getSelfMentionPattern() {
        return selfMention == null ? selfMention = Pattern.compile("<@!?" + Bot.getPrimaryShard().getSelfUser().getIdLong() + ">") : selfMention;
    }

    public static void onBotMentioned(MessageReceivedEvent e) {
        e.getChannel()
            .sendMessage("Prefix: " + Bot.PREFIX)
            .setEmbeds(new EmbedBuilder()
                .setAuthor("V Play Games Bot Info")
                .setDescription("A Pokemon-related discord bot created, developed & maintained by")
                .appendDescription(" V Play Games aka VPG (<@" + Bot.BOT_OWNER + ">)")
                .addField("Developer", "<@" + Bot.BOT_OWNER + ">", true)
                .addField("Version", Bot.VERSION, true)
                .addField("Server Count", String.valueOf(Bot.getShardManager().getGuilds().size()), false)
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
                String[] args = Util.DELIMITER.split(content);
                if (content.regionMatches(true, 0, Bot.PREFIX, 0, Bot.PREFIX.length())) {
                    Optional.ofNullable(Bot.commands.get(args[0].substring(Bot.PREFIX.length()).toLowerCase()))
                        .ifPresent(command -> CommandReceivedEvent.run(e, args, command));
                } else if (getSelfMentionPattern().matcher(content).find()) {
                    onBotMentioned(e);
                }
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
            // init on last shard only
            if (shardsInit.get() == e.getJDA().getShardInfo().getShardTotal() - 1)
                Bot.init();
            else
                shardsInit.incrementAndGet();
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public void onButtonClick(@Nonnull ButtonClickEvent e) {
        String method = Util.getMethod(e.getComponentId());
        String[] args = Util.getArgs(e.getComponentId()).split(":");
        Bot.buttonHandlers.get(method).handle(e, args);
    }
}
