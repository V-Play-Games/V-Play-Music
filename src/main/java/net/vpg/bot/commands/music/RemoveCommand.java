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
import net.vpg.bot.core.VPMUtil;
import net.vpg.bot.framework.Bot;
import net.vpg.bot.framework.commands.BotCommandImpl;
import net.vpg.bot.framework.commands.CommandReceivedEvent;
import net.vpg.bot.player.PlayerManager;

import java.util.LinkedList;

public class RemoveCommand extends BotCommandImpl {
    public RemoveCommand(Bot bot) {
        super(bot, "remove", "Remove the given track from the queue");
        addOption(OptionType.INTEGER, "index", "Index of the track to be removed", true);
        setMinArgs(1);
        setMaxArgs(1);
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e, VPMUtil.toInt(e.getArg(1)));
    }

    @Override
    public void onSlashCommandRun(CommandReceivedEvent e) {
        execute(e, (int) e.getLong("index"));
    }

    public void execute(CommandReceivedEvent e, int index) {
        LinkedList<AudioTrack> queue = PlayerManager.getPlayer(e).getQueue();
        if (index < 1 || index > queue.size()) {
            e.send("Invalid index.").queue();
            return;
        }
        AudioTrack track = queue.remove(index - 1);
        if (track == null) {
            e.send("Something went wrong, Please try again later").queue();
            return;
        }
        e.send("Removed " + VPMUtil.toString(track)).queue();
    }
}
