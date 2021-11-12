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

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vpg.bot.core.VPMUtil;
import net.vpg.bot.framework.Bot;
import net.vpg.bot.framework.commands.BotCommandImpl;
import net.vpg.bot.framework.commands.CommandReceivedEvent;
import net.vpg.bot.player.PlayerManager;

public class PlayCommand extends BotCommandImpl {
    public PlayCommand(Bot bot) {
        super(bot, "play", "Play a track", "p");
        setMinArgs(1);
        addOption(OptionType.STRING, "track", "Type the name or URL of the song you want to play", true);
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e, String.join(" ", e.getArgsFrom(1)));
    }

    @Override
    public void onSlashCommandRun(CommandReceivedEvent e) {
        execute(e, e.getString("track"));
    }

    public void execute(CommandReceivedEvent e, String track) {
        if (!VPMUtil.canJoinVC(e)) {
            return;
        }
        while (track.startsWith("<") && track.endsWith(">")) {
            track = track.substring(1, track.length() - 1);
        }
        if (!VPMUtil.isUri(track) && !track.startsWith("scsearch:")) {
            track = "ytsearch:" + track;
        }
        PlayerManager.getManager(bot).loadAndPlay(e, track, false);
    }
}
