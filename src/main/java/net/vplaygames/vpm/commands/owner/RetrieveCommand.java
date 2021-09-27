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
package net.vplaygames.vpm.commands.owner;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vplaygames.vpm.commands.AbstractBotCommand;
import net.vplaygames.vpm.commands.OwnerCommand;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.Util;

import java.util.Objects;
import java.util.stream.Collectors;

public class RetrieveCommand extends AbstractBotCommand implements OwnerCommand {
    public RetrieveCommand() {
        super("retrieve", "Request data from the bot");
        addOption(OptionType.STRING, "key","Key of the information required", true);
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e, e.getArg(1));
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) {
        execute(e, Util.getString(slash, "key"));
    }

    public void execute(CommandReceivedEvent e, String arg) {
        switch (arg) {
//            case "logs":
//                e.send("`java.lang.System.out`").addFile(new FileInputStream((FileDescriptor.out)), "log-file.txt").queue();
//                break;
//            case "errors":
//                e.send("`java.lang.System.err`").addFile(new FileInputStream(FileDescriptor.err), "err-file.txt").queue();
//                break;
            case "vc":
                VoiceChannel vc0 = e.getSelfMember().getVoiceState().getChannel();
                e.send(vc0 == null ? null : vc0.toString()
                    + "\nConnected Members: " + Util.getListeningMembers(vc0)
                    .stream()
                    .map(Member::getUser)
                    .collect(Collectors.toList()) + "\n"
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
                        + "\nConnected Members: " + Util.getListeningMembers(vc)
                        .stream()
                        .map(Member::getUser)
                        .collect(Collectors.toList()) + "\n"
                        + vc.getGuild().getOwner().toString())
                    .forEach(s -> e.send(s).queue());
                break;
            default:
                e.send("idk what you want :/").queue();
        }
    }
}
