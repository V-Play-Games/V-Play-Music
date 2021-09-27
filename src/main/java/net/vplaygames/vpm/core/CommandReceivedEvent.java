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
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.vplaygames.vpm.commands.AbstractBotCommand;

import javax.annotation.CheckReturnValue;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

public class CommandReceivedEvent implements Sender {
    public final long messageId;
    public final long processId;
    public final MessageChannel channel;
    public final JDA api;
    public final Guild guild;
    public final User author;
    public final Member member;
    public final OffsetDateTime timeCreated;
    public final String content;
    public final AbstractBotCommand command;
    public final Member selfMember;
    public Message message;
    public SlashCommandEvent slash;
    public boolean isSlashCommand;
    public boolean forceNotLog;
    public CommandReplyAction action;
    public String output = "";
    public Throwable trouble;
    private List<String> args;

    public CommandReceivedEvent(MessageReceivedEvent e, String[] args, AbstractBotCommand command) {
        this(e.getJDA(),
            e.getMessageIdLong(),
            e.getChannel(),
            e.getGuild(),
            e.getAuthor(),
            e.getMember(),
            e.getMessage().getContentRaw(),
            command,
            e.getMessage().getTimeCreated(),
            false);
        this.args = Arrays.asList(args);
        this.message = e.getMessage();
        action = new CommandReplyAction(null, message, this::log);
    }

    public CommandReceivedEvent(SlashCommandEvent e, AbstractBotCommand command) {
        this(e.getJDA(),
            e.getIdLong(),
            e.getChannel(),
            e.getGuild(),
            e.getUser(),
            e.getMember(),
            e.getCommandString(),
            command,
            e.getTimeCreated(),
            true);
        slash = e;
        action = new CommandReplyAction(e, null, this::log);
    }

    public CommandReceivedEvent(JDA api,
                                long messageId,
                                MessageChannel channel,
                                Guild guild,
                                User author,
                                Member member,
                                String content,
                                AbstractBotCommand command,
                                OffsetDateTime timeCreated,
                                boolean isSlashCommand) {
        this.messageId = messageId;
        this.channel = channel;
        this.api = api;
        this.guild = guild;
        this.author = author;
        this.member = member;
        this.content = content;
        this.command = command;
        this.timeCreated = timeCreated;
        this.isSlashCommand = isSlashCommand;
        this.processId = Bot.lastCommandId.getAndIncrement();
        this.selfMember = guild.getMember(api.getSelfUser());
    }

    public static void run(MessageReceivedEvent e, String[] args, AbstractBotCommand command) {
        command.run(new CommandReceivedEvent(e, args, command));
    }

    public static void run(SlashCommandEvent e, AbstractBotCommand command) {
        command.run(new CommandReceivedEvent(e, command));
    }

    public Member getSelfMember() {
        return selfMember;
    }

    public List<String> getArgs() {
        return args;
    }

    public JDA getJDA() {
        return api;
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public long getMessageIdLong() {
        return messageId;
    }

    public boolean isFromType(ChannelType type) {
        return channel.getType() == type;
    }

    public boolean isFromGuild() {
        return this.getChannelType().isGuild();
    }

    public ChannelType getChannelType() {
        return channel.getType();
    }

    public Guild getGuild() {
        return guild;
    }

    public User getAuthor() {
        return author;
    }

    public Member getMember() {
        return member;
    }

    public List<String> getArgsFrom(int index) {
        return args.subList(index, args.size());
    }

    public String getArg(int index) {
        return args.get(index);
    }

    public void responded(String response) {
        output += response + "\n";
    }

    @CheckReturnValue
    public CommandReplyAction send(String content) {
        responded(content);
        action.append(content);
        return action;
    }

    @CheckReturnValue
    public CommandReplyAction send(MessageEmbed embed, String placeholder) {
        responded(placeholder);
        action.addEmbeds(embed);
        return action;
    }

    public void reportTrouble(Throwable t) {
        trouble = t;
        t.printStackTrace();
    }

    public void forceNotLog() {
        forceNotLog = true;
    }

    public void log() {
        DataObject logRepresentation = DataObject.empty();
        logRepresentation.put("id", processId);
        logRepresentation.put("time", timeCreated.toEpochSecond());
        logRepresentation.put("content", content);
        logRepresentation.put("output", output);
        logRepresentation.put("args", args);
        logRepresentation.put("command", command.toString());
        logRepresentation.put("userId", getAuthor().getIdLong());
        logRepresentation.put("channelId", getChannel().getIdLong());
        logRepresentation.put("channelName", getChannel().getName());
        logRepresentation.put("messageId", getMessageIdLong());
        logRepresentation.put("trouble", trouble);
        if (isFromGuild()) {
            logRepresentation.put("guild", getGuild().getIdLong());
            logRepresentation.put("guildName", getGuild().getName());
        }
        if (!forceNotLog) {
            Bot.getLogChannel(getJDA()).sendMessageEmbeds(new EmbedBuilder()
                .setTitle("Process id " + processId)
                .setDescription("Error: " + (trouble == null
                    ? "None"
                    : trouble.getClass() + ": " + trouble.getMessage() + "\n\t at " + trouble.getStackTrace()[0]) +
                    "\nUsed in " + (!isFromGuild() ? "the DM of " : "#" + getChannel().toString() + " by ")
                    + getAuthor().toString())
                .addField("Input", content.length() > 1024 ? content.substring(0, 1021) + "..." : content, false)
                .addField("Output", output.length() > 1024 ? output.substring(0, 1021) + "..." : output, false)
                .build())
                .addFile(Util.makeFileOf(logRepresentation, "log-file-" + processId + ".json"))
                .queue();
        }
        output = "";
        action.setContent("");
    }
}
