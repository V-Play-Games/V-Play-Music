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

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.commands.OwnerCommand;
import net.vplaygames.TheChaosTrilogy.core.Bot;

public class LogCommand extends OwnerCommand {
    public LogCommand() {
        super("log", "Sets log channel to the current channel");
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) {
        execute(e, slash.getChannel());
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e, e.getChannel());
    }

    public void execute(CommandReceivedEvent e, MessageChannel channel) {
        Bot.setLogChannel((TextChannel) channel);
        e.send("Log opened in " + Bot.logChannel.getAsMention()).queue();
    }
}
