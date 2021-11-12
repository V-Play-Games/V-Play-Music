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

public class MoveCommand extends BotCommandImpl {
    public MoveCommand(Bot bot) {
        super(bot, "move", "Move songs through the queue");
        addOption(OptionType.INTEGER, "from", "Index of the track to be removed", true);
        addOption(OptionType.INTEGER, "to", "Index of the track to be removed", true);
        setMinArgs(2);
        setMaxArgs(2);
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e, VPMUtil.toInt(e.getArg(2)) - 1, VPMUtil.toInt(e.getArg(3)) - 1);
    }

    @Override
    public void onSlashCommandRun(CommandReceivedEvent e) {
        execute(e, (int) e.getLong("from") - 1, (int) e.getLong("to") - 1);
    }

    public void execute(CommandReceivedEvent e, int from, int to) {
        if (!VPMUtil.canJoinVC(e)) {
            return;
        }
        LinkedList<AudioTrack> queue = PlayerManager.getPlayer(e).getQueue();
        if (e.getArgs().size() != 4) {
            e.send("Please provide a proper amount of arguments")
                .append("\nFormat: `")
                .append(e.getPrefix())
                .append(getName())
                .append(" move <from_index> <to_index>`")
                .queue();
            return;
        }
        if (from < 0 || to < 0) {
            e.send("Index cannot be in negative!").queue();
        }
        if (from >= queue.size() || to >= queue.size()) {
            e.send("Index more than the queue size!").queue();
        }
        AudioTrack toMove = queue.get(from);
        AudioTrack oldTrack = queue.set(to, toMove);
        queue.set(from, oldTrack);
        e.send("Switched " + from + " and " + to).queue();
    }
}
