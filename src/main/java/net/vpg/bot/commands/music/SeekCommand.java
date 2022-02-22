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
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.core.Bot;
import net.vpg.bot.core.Util;
import net.vpg.bot.core.VPMUtil;
import net.vpg.bot.event.CommandReceivedEvent;
import net.vpg.bot.event.SlashCommandReceivedEvent;
import net.vpg.bot.event.TextCommandReceivedEvent;
import net.vpg.bot.player.PlayerManager;

public class SeekCommand extends BotCommandImpl {
    public SeekCommand(Bot bot) {
        super(bot, "seek", "Remove the given track from the queue");
        addOption(OptionType.INTEGER, "position", "Index of the track to be removed", true);
        setMinArgs(1);
        setMaxArgs(1);
    }

    @Override
    public void onTextCommandRun(TextCommandReceivedEvent e) {
        execute(e, VPMUtil.toInt(e.getArg(1)));
    }

    @Override
    public void onSlashCommandRun(SlashCommandReceivedEvent e) {
        execute(e, e.getLong("position"));
    }

    public void execute(CommandReceivedEvent e, long position) {
        AudioTrack track = PlayerManager.getPlayer(e).getPlayingTrack();
        if (track == null) {
            e.send("Nothin' Playin' in 'ere").queue();
            return;
        }
        position *= 1000;
        if (position < 0 || position > track.getDuration()) {
            e.send("Position out of bounds! Must be between 0 and the length of the track (" + Util.toString(track.getDuration()) + ")").queue();
            return;
        }
        track.setPosition(position);
        e.send("Seeked to " + Util.toString(position)).queue();
    }
}
