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

import net.vpg.bot.commands.manager.WipeCommand;
import net.vpg.bot.framework.Bot;
import net.vpg.bot.framework.commands.CommandReceivedEvent;

public class WipeCommandImpl extends WipeCommand {
    public WipeCommandImpl(Bot bot) {
        super(bot);
        setMinArgs(1);
        setMaxArgs(1);
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        switch (e.getArg(1)) {
            case "all":
            case "ratelimit":
                bot.getCommands().values().forEach((command) -> command.getRateLimited().clear());
                break;
            default:
                e.send("wat").queue();
                return;
        }
        e.send("Data Wiped!").queue();
    }
}
