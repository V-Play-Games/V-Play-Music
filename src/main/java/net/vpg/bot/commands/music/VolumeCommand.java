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

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.core.Bot;
import net.vpg.bot.core.VPMUtil;
import net.vpg.bot.event.CommandReceivedEvent;
import net.vpg.bot.event.SlashCommandReceivedEvent;
import net.vpg.bot.event.TextCommandReceivedEvent;

public class VolumeCommand extends BotCommandImpl {
    public VolumeCommand(Bot bot) {
        super(bot, "volume", "Shows info on currently playing track if any");
        addOption(OptionType.INTEGER, "volume", "An integer in the range of 1 - 1000", true);
        setMaxArgs(1);
        setMinArgs(1);
    }

    @Override
    public void onTextCommandRun(TextCommandReceivedEvent e) {
        execute(e, VPMUtil.toInt(e.getArg(1)));
    }

    @Override
    public void onSlashCommandRun(SlashCommandReceivedEvent e) {
        execute(e, (int) e.getLong("volume"));
    }

    public void execute(CommandReceivedEvent e, int volume) {
        e.send("Operation Not Supported.").queue();
    }
}
