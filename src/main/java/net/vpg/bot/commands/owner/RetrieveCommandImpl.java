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
package net.vpg.bot.commands.owner;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.vpg.bot.commands.manager.RetrieveCommand;
import net.vpg.bot.core.VPMUtil;
import net.vpg.bot.framework.Bot;
import net.vpg.bot.framework.commands.CommandReceivedEvent;

import java.util.Objects;
import java.util.stream.Collectors;

public class RetrieveCommandImpl extends RetrieveCommand {
    public RetrieveCommandImpl(Bot bot) {
        super(bot);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void execute(CommandReceivedEvent e, String arg) {
        switch (arg) {
            case "vc":
                VoiceChannel vc0 = e.getSelfMember().getVoiceState().getChannel();
                e.send(vc0 == null ? null : vc0.toString()
                    + "\nConnected Members: " + VPMUtil.getListeningMembers(vc0)
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
                        + "\nConnected Members: " + VPMUtil.getListeningMembers(vc)
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
