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
package net.vplaygames.TheChaosTrilogy.commands;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.vplaygames.TheChaosTrilogy.core.Bot;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;

public abstract class OwnerCommand extends AbstractBotCommand {
    protected OwnerCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
        setDefaultEnabled(false);
    }

    @Override
    public void finalizeCommand(Command c) {
//        c.updatePrivileges(c.getJDA().getGuildById(846376417821720606L), CommandPrivilege.enableUser(Bot.BOT_OWNER)).queue();
    }

    @Override
    public boolean runChecks(CommandReceivedEvent e) {
        if (e.getAuthor().getIdLong() != Bot.BOT_OWNER) {
            e.send("You do not have the permission to do that!").queue();
            return false;
        }
        return true;
    }
}
