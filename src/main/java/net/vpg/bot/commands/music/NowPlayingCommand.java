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
package net.vpg.bot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.vpg.bot.core.VPMUtil;
import net.vpg.bot.framework.Bot;
import net.vpg.bot.framework.Util;
import net.vpg.bot.framework.commands.BotCommandImpl;
import net.vpg.bot.framework.commands.CommandReceivedEvent;
import net.vpg.bot.framework.commands.NoArgsCommand;
import net.vpg.bot.player.PlayerManager;

public class NowPlayingCommand extends BotCommandImpl implements NoArgsCommand {
    public NowPlayingCommand(Bot bot) {
        super(bot, "nowplaying", "Shows info on currently playing track if any", "np");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        AudioTrack track = PlayerManager.getPlayer(e).getPlayingTrack();
        if (track == null) {
            e.send("There's nothin' playin' in 'ere. Party's o'er. Let's have an after party whaddaya think?").queue();
            return;
        }
        AudioTrackInfo info = track.getInfo();
        e.sendEmbeds(new EmbedBuilder()
            .setTitle(info.title, info.uri)
            .appendDescription(VPMUtil.getProgressBar(track))
            .appendDescription(" ")
            .appendDescription(Util.toString(track.getPosition()))
            .appendDescription("/")
            .appendDescription(Util.toString(track.getDuration()))
            .build()).queue();
    }
}
