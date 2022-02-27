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

import net.vpg.bot.commands.MusicCommand;
import net.vpg.bot.commands.NoArgsCommand;
import net.vpg.bot.core.Bot;
import net.vpg.bot.core.VPMUtil;
import net.vpg.bot.event.CommandReceivedEvent;
import net.vpg.bot.player.MusicPlayer;
import net.vpg.bot.player.PlayerManager;

public class ResumeCommand extends MusicCommand implements NoArgsCommand {
    public ResumeCommand(Bot bot) {
        super(bot, "resume", "Stops the current song");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        if (!VPMUtil.canJoinVC(e)) {
            return;
        }
        MusicPlayer player = PlayerManager.getPlayer(e);
        if (player.getPlayingTrack() == null) {
            e.send("Resume? Resume what? Nothin' playin' in 'ere.").queue();
            return;
        }
        if (!player.isPaused()) {
            e.send("Resume? Resume what? Isn't it playing already?").queue();
            return;
        }
        player.setPaused(false);
        e.send("Resumed the track.").queue();
    }
}
