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

import net.dv8tion.jda.api.entities.Member;
import net.vpg.bot.core.VPMUtil;
import net.vpg.bot.framework.Bot;
import net.vpg.bot.framework.commands.BotCommandImpl;
import net.vpg.bot.framework.commands.CommandReceivedEvent;
import net.vpg.bot.framework.commands.NoArgsCommand;
import net.vpg.bot.player.MusicPlayer;
import net.vpg.bot.player.PlayerManager;

import java.util.List;

public class SkipCommand extends BotCommandImpl implements NoArgsCommand {
    public SkipCommand(Bot bot) {
        super(bot, "skip", "Skips the current playing track if any", "s");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        if (!VPMUtil.canJoinVC(e)) {
            return;
        }
        MusicPlayer player = PlayerManager.getPlayer(e);
        if (player.getPlayingTrack() == null) {
            e.send("There's nothin' playin' in 'ere. Party's over. Let's have an after-party whaddaya think?").queue();
            return;
        }
        List<Member> listeningMembers = VPMUtil.getListeningMembers(player.getConnectedVC());
        if (listeningMembers.size() == 1 && listeningMembers.get(0).equals(e.getMember())) {
            player.playNext();
            e.send("Successfully skipped!").queue();
            return;
        }
        player.skip(e, listeningMembers);
    }
}
