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
package net.vplaygames.TheChaosTrilogy.commands.fun;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vplaygames.TheChaosTrilogy.commands.AbstractBotCommand;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;

public class ReverseCommand extends AbstractBotCommand {
    public ReverseCommand() {
        super("reverse", "Reverses the given text");
        addOption(OptionType.STRING, "text", "the text to reverse", true);
        setMinArgs(1);
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e, e.content.replace(e.getArg(0) + " ", ""));
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) {
        execute(e, slash.getOption("text").getAsString());
    }

    public void execute(CommandReceivedEvent e, String reverse) {
        e.send(new StringBuilder(reverse).reverse().toString()).queue();
    }
}
