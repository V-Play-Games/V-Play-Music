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
package net.vplaygames.vpm.commands.fun;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.vplaygames.vpm.commands.AbstractBotCommand;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.Util;

public class CaseCommand extends AbstractBotCommand {
    public CaseCommand() {
        super("case", "Changes the case of the given content");
        addOptions(
            new OptionData(OptionType.STRING, "text", "the content to change the case of", true),
            new OptionData(OptionType.STRING, "case", "The case to change to", true)
                .addChoice("upper", "Upper Case")
                .addChoice("lower", "Lower Case")
                .addChoice("proper", "Proper Case")
        );
        setMinArgs(2);
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e,
            e.content.substring(e.content.lastIndexOf(e.getArg(1)) + 1),
            e.getArg(1).toLowerCase());
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) {
        execute(e,
            slash.getOption("text").getAsString(),
            slash.getOption("case").getAsString());
    }

    public void execute(CommandReceivedEvent e, String input, String theCase) {
        switch (theCase) {
            case "upper":
            case "u":
                input = input.toUpperCase();
                break;
            case "lower":
            case "l":
                input = input.toLowerCase();
                break;
            case "proper":
            case "p":
                input = Util.toProperCase(input);
                break;
            default:
                input = "Invalid Case!";
        }
        e.send(input).queue();
    }
}
