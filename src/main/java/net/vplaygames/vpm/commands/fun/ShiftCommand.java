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
import net.vplaygames.vpm.commands.AbstractBotCommand;
import net.vplaygames.vpm.core.CommandReceivedEvent;

public class ShiftCommand extends AbstractBotCommand {
    public static final String chars = "abcdefghijklmnopqrstuvwxyz";
    public static final String sheet = chars + chars;

    public ShiftCommand() {
        super("shift", "Shift the alphabets of the given text");
        addOption(OptionType.STRING, "text", "the text to shift alphabets of", true);
        addOption(OptionType.INTEGER, "offset", "the amount of alphabets to shift", true);
        setMinArgs(2);
    }

    static char shiftChar(char c, int shift) {
        boolean upperCase = Character.isUpperCase(c);
        char alphaStart = upperCase ? 'A' : 'a';
        char alphaEnd = upperCase ? 'Z' : 'z';
        if (c > alphaEnd || c < alphaStart)
            return c;
        int shifted = c + shift;
        return (char) (shifted > alphaEnd ? shifted - alphaEnd + alphaStart : shifted);
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e, String.join(" ", e.getArgsFrom(2)), Long.parseLong(e.getArg(1)));
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) {
        execute(e,
            slash.getOption("text").getAsString(),
            slash.getOption("offset").getAsLong());
    }

    public void execute(CommandReceivedEvent e, String text, long offset) {
        StringBuilder sb = new StringBuilder();
        int shift = (int) (offset < 0 ? 26 + (offset % 26) : offset % 26);
        if (shift == 0) {
            sb.append(text);
        } else {
            for (int i = 0; i < text.length(); i++) {
                sb.append(shiftChar(text.charAt(i), shift));
            }
        }
        e.send(sb.toString()).queue();
    }
}
