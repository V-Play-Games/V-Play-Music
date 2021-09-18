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
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vplaygames.TheChaosTrilogy.commands.OwnerCommand;
import net.vplaygames.TheChaosTrilogy.core.Bot;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Util;

public class WipeCommand extends OwnerCommand {
    public WipeCommand() {
        super("wipe", "Wipes data from the bot");
        addOption(OptionType.STRING, "name", "the data to wipe from the bot");
        setMinArgs(1);
        setMaxArgs(1);
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) {
        execute(e, Util.getString(slash, "name", "all"));
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e, e.getArg(1));
    }

    private void execute(CommandReceivedEvent e, String toWipe) {
        switch (toWipe) {
            case "all":
            case "ratelimit":
                Bot.commands.values().forEach((command) -> command.getRateLimited().clear());
                break;
            default:
                e.send("wat").queue();
                return;
        }
        e.send("Data Wiped!").queue();
    }
}
