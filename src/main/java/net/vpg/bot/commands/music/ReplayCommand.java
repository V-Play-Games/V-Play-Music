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
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.commands.CommandReceivedEvent;
import net.vpg.bot.commands.NoArgsCommand;
import net.vpg.bot.framework.Bot;
import net.vpg.bot.player.PlayerManager;

public class ReplayCommand extends BotCommandImpl implements NoArgsCommand {
    public ReplayCommand(Bot bot) {
        super(bot, "replay", "Remove the given track from the queue");
    }

    public void execute(CommandReceivedEvent e) {
        AudioTrack track = PlayerManager.getPlayer(e).getPlayingTrack();
        if (track == null) {
            e.send("Nothin' Playin' in 'ere").queue();
            return;
        }
        track.setPosition(0);
        e.send("Replayed from start").queue();
    }
}
