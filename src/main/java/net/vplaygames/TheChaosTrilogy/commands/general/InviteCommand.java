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
package net.vplaygames.TheChaosTrilogy.commands.general;

import net.dv8tion.jda.api.EmbedBuilder;
import net.vplaygames.TheChaosTrilogy.commands.SharedImplementationCommand;
import net.vplaygames.TheChaosTrilogy.core.Bot;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;

public class InviteCommand extends SharedImplementationCommand {
    public InviteCommand() {
        super("invite", "sends a link to add the bot in the server and other links");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        e.send(new EmbedBuilder()
            .setDescription(
                "[Add the bot to your server](" + e.getJDA().getInviteUrl() + "+applications.commands)\n" +
                    "[Join the bot's support server](" + Bot.SUPPORT_SERVER_INVITE + ")"
            ).build(), "invite URLs").queue();
    }
}
