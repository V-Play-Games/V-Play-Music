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

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.commands.OwnerCommand;
import net.vplaygames.TheChaosTrilogy.core.Bot;
import net.vplaygames.TheChaosTrilogy.core.Util;

import java.io.File;

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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void execute(CommandReceivedEvent e) {
        String[] msg = e.content.split(" ");
        switch (msg[1]) {
            case "logs":
                File temp = Util.makeFileOf(Bot.responses, "logs-" + System.currentTimeMillis());
                e.send("Here!", temp).queue(x -> temp.delete());
                break;
            case "logFile":
                e.send("logFile", Bot.logFile).queue();
                break;
            case "errorFile":
                e.send("errorFile", Bot.errorFile).queue();
                break;
            default:
                e.send("idk what you want :/").queue();
        }
    }
}