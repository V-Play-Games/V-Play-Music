/*
 * Copyright 2020-2021 Vaibhav Nargwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.vplaygames.TheChaosTrilogy.commands.owner;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.vplaygames.TheChaosTrilogy.commands.OwnerCommand;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Util;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.util.Objects;

public class RetrieveCommand extends OwnerCommand {
    public RetrieveCommand() {
        super("retrieve", "Request data from the bot");
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e);
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) {
        execute(e);
    }

    public void execute(CommandReceivedEvent e) {
        String[] msg = e.content.split(" ");
        switch (msg[1]) {
            case "logs":
                e.send("`java.lang.System.out`").addFile(new FileInputStream(FileDescriptor.out), "log-file.txt").queue();
                break;
            case "errors":
                e.send("`java.lang.System.err`").addFile(new FileInputStream(FileDescriptor.err), "err-file.txt").queue();
                break;
            case "vc":
                VoiceChannel vc0 = e.getSelfMember().getVoiceState().getChannel();
                e.getChannel().sendMessage(vc0.toString()
                    + "\nConnected Members: " + Util.getListeningMembers(vc0) + "\n"
                    + vc0.getGuild().getOwner().toString()).queue();
                break;
            case "vc-all":
                e.getJDA()
                    .getGuilds()
                    .stream()
                    .map(guild -> guild.getMember(e.getJDA().getSelfUser()))
                    .map(Member::getVoiceState)
                    .map(GuildVoiceState::getChannel)
                    .filter(Objects::nonNull)
                    .map(vc -> vc.toString()
                        + "\nConnected Members: " + Util.getListeningMembers(vc) + "\n"
                        + vc.getGuild().getOwner().toString())
                    .forEach(s -> e.getChannel().sendMessage(s).queue());
                break;
            default:
                e.send("idk what you want :/").queue();
        }
    }
}
