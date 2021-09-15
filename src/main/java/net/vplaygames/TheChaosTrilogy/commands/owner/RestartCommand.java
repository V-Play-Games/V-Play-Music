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

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.utils.TimeFormat;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.commands.OwnerCommand;
import net.vplaygames.TheChaosTrilogy.core.Bot;

import java.time.Instant;
import java.util.function.Consumer;

import static net.vplaygames.TheChaosTrilogy.core.Bot.*;

public class RestartCommand extends OwnerCommand {
    public RestartCommand() {
        super("restart", "Restarts the bot");
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        e.forceNotLog();
        e.getChannel().sendMessage("Trying a restart!")
            .queue(message -> execute(e.getJDA(), s -> message.editMessage(s).queue()));
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) throws Exception {
        e.forceNotLog();
        slash.reply("Trying a restart!")
            .queue(message -> execute(slash.getJDA(), s -> slash.getHook().editOriginal(s).queue()));
    }

    public void execute(JDA jda, Consumer<String> editor) {
        timer.execute(() -> {
            long startedAt = System.currentTimeMillis();
            Instant oldInstant = instantAtBoot;
            System.out.println("Shutting Down");
            jda.shutdown();
            rebootTasks = () -> {
                rebooted = true;
                editor.accept("Restart Successfully Completed! Took "
                    + (System.currentTimeMillis() - startedAt) + "ms");
                instantAtBoot = oldInstant;
                lastRefresh = TimeFormat.RELATIVE.now().toString();
            };
            while (true) {
                try {
                    Bot.start();
                    return;
                } catch (Exception ignored) {
                }
            }
        });
    }
}