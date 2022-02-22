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
import net.vpg.bot.core.VPMUtil;
import net.vpg.bot.event.CommandReceivedEvent;
import net.vpg.bot.event.SlashCommandReceivedEvent;
import net.vpg.bot.event.TextCommandReceivedEvent;
import net.vpg.bot.player.PlayerManager;

import java.util.LinkedList;

public class SwapCommand extends BotCommandImpl {
    public SwapCommand(Bot bot) {
        super(bot, "swap", "Swap songs through the queue");
        addOption(OptionType.INTEGER, "swap1", "Index of the first track to swap", true);
        addOption(OptionType.INTEGER, "swap2", "Index of the second track to swap", true);
        setMinArgs(2);
        setMaxArgs(2);
    }

    @Override
    public void onTextCommandRun(TextCommandReceivedEvent e) {
        execute(e, VPMUtil.toInt(e.getArg(2)) - 1, VPMUtil.toInt(e.getArg(3)) - 1);
    }

    @Override
    public void onSlashCommandRun(SlashCommandReceivedEvent e) {
        execute(e, (int) e.getLong("swap1") - 1, (int) e.getLong("swap2") - 1);
    }

    public void execute(CommandReceivedEvent e, int swap1, int swap2) {
        if (!VPMUtil.canJoinVC(e)) return;
        LinkedList<AudioTrack> queue = PlayerManager.getPlayer(e).getQueue();
        if (swap1 == swap2) {
            e.send("Cannot swap the same index!").queue();
            return;
        }
        if (swap1 < 0 || swap2 < 0) {
            e.send("Index cannot be in negative!").queue();
            return;
        }
        if (swap1 >= queue.size() || swap2 >= queue.size()) {
            e.send("Index more than the queue size!").queue();
            return;
        }
        queue.set(swap1, queue.set(swap2, queue.get(swap1)));
        e.send("Swapped " + swap1 + " and " + swap2).queue();
    }
}
