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

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Member;
import net.vpg.bot.commands.MusicCommand;
import net.vpg.bot.core.Bot;
import net.vpg.bot.core.VPMUtil;
import net.vpg.bot.event.CommandReceivedEvent;
import net.vpg.bot.event.SlashCommandReceivedEvent;
import net.vpg.bot.event.TextCommandReceivedEvent;
import net.vpg.bot.player.MusicPlayer;
import net.vpg.bot.player.PlayerManager;

import java.util.List;

public class LeaveCommand extends MusicCommand {
    public LeaveCommand(Bot bot) {
        super(bot, "leave", "Leaves the current VC", "disconnect", "dc");
    }

    @SuppressWarnings("ConstantConditions")
    public void execute(CommandReceivedEvent e) {
        AudioChannel audio = e.getSelfMember().getVoiceState().getChannel();
        if (audio == null) {
            e.send("Leave? Leave what?").queue();
            return;
        }
        List<Member> members = VPMUtil.getListeningMembers(audio);
        if (members.isEmpty() ||
            (members.size() == 1 && members.get(0).equals(e.getMember())) ||
            e.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            PlayerManager.getPlayer(e).destroy();
            e.send("Left " + audio.getAsMention()).queue();
            return;
        }
        e.send("No. I am vibin' with my people in 'ere, I can't leave 'em alone like that :(").queue();
    }

    @Override
    public void onTextCommandRun(TextCommandReceivedEvent e) {
        if (bot.isManager(e.getUser().getIdLong()) && e.getArgs().size() != 1) {
            if (e.getArg(0).equals("all")) {
                PlayerManager.getManager(bot).forEach(MusicPlayer::destroy);
                e.send("DC'ed from everywhere, just for you senpai ;)").queue();
            } else if (e.getArg(0).equals("this")) {
                PlayerManager.getPlayer(e).destroy();
                e.send("DC'ed from here, just for you senpai ;)").queue();
            } else {
                e.send("wut").queue();
            }
            return;
        }
        execute(e);
    }

    @Override
    public void onSlashCommandRun(SlashCommandReceivedEvent e) {
        execute(e);
    }
}
