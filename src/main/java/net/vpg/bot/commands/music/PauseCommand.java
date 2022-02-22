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

import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.commands.NoArgsCommand;
import net.vpg.bot.core.Bot;
import net.vpg.bot.core.VPMUtil;
import net.vpg.bot.event.CommandReceivedEvent;
import net.vpg.bot.player.MusicPlayer;
import net.vpg.bot.player.PlayerManager;

public class PauseCommand extends BotCommandImpl implements NoArgsCommand {
    public PauseCommand(Bot bot) {
        super(bot, "pause", "Stops the current song");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        if (!VPMUtil.canJoinVC(e)) {
            return;
        }
        MusicPlayer player = PlayerManager.getPlayer(e);
        if (player.getPlayingTrack() == null) {
            e.send("Pause? Pause what? Nothin' playin' in 'ere.").queue();
            return;
        }
        if (player.isPaused()) {
            e.send("Pause? Pause what? Isn't it paused already?").queue();
            return;
        }
        player.setPaused(true);
        e.send("Paused the track.").queue();
    }
}
