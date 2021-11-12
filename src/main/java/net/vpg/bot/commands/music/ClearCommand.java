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

import net.vpg.bot.core.VPMUtil;
import net.vpg.bot.framework.Bot;
import net.vpg.bot.framework.commands.BotCommandImpl;
import net.vpg.bot.framework.commands.CommandReceivedEvent;
import net.vpg.bot.framework.commands.NoArgsCommand;
import net.vpg.bot.player.PlayerManager;

public class ClearCommand extends BotCommandImpl implements NoArgsCommand {
    public ClearCommand(Bot bot) {
        super(bot, "clear", "Clear the queue");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        if (!VPMUtil.canJoinVC(e)) {
            return;
        }
        PlayerManager.getPlayer(e).getQueue().clear();
        e.send("Boom! Queue empty.").queue();
    }
}
