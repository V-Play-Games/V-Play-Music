/*
 * Copyright 2021 Vaibhav Nargwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.vpg.bot.commands.general;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.vpg.bot.commands.CommandReceivedEvent;
import net.vpg.bot.framework.Bot;

import java.time.Instant;

public class InfoCommandImpl extends InfoCommand {
    public InfoCommandImpl(Bot bot) {
        super(bot, "info about the bot");
    }

    @Override
    protected MessageEmbed getEmbed(CommandReceivedEvent e) {
        return new EmbedBuilder()
            .setAuthor("V Play Music Bot Info")
            .setDescription("A music bot with basic commands to play commands from YouTube, SoundCloud and Vimeo with search, loop and queue support")
            .addField("Developer", "<@" + bot.getOwnerId() + ">", true)
            .addField("Version", "", true)
            .addField("Server Count", String.valueOf(e.getJDA().getGuilds().size()), false)
            .setTimestamp(Instant.now())
            .setThumbnail(e.getJDA().getSelfUser().getAvatarUrl())
            .setColor(0x1abc9c)
            .build();
    }
}