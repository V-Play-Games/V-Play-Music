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
package net.vplaygames.vpm.commands.owner;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.vplaygames.vpm.commands.OwnerCommand;
import net.vplaygames.vpm.core.Bot;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.player.MusicPlayer;
import net.vplaygames.vpm.player.PlayerManager;

public class ShutdownCommand extends OwnerCommand {
    public ShutdownCommand() {
        super("shutdown", "Restarts the bot");
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e);
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) {
        execute(e);
    }

    public void execute(CommandReceivedEvent e) {
        e.send("Shutting Down!").queue();
        PlayerManager.getInstance().forEach(MusicPlayer::destroy);
        Bot.getShardManager().shutdown();
    }
}
