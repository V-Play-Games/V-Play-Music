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

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.commands.NoArgsCommand;
import net.vpg.bot.core.Bot;
import net.vpg.bot.core.ButtonHandler;
import net.vpg.bot.core.Util;
import net.vpg.bot.core.VPMUtil;
import net.vpg.bot.event.BotButtonEvent;
import net.vpg.bot.event.CommandReceivedEvent;
import net.vpg.bot.player.MusicPlayer;
import net.vpg.bot.player.PlayerManager;

import java.util.LinkedList;
import java.util.List;

public class QueueCommand extends BotCommandImpl implements NoArgsCommand {
    public QueueCommand(Bot bot) {
        super(bot, "queue", "View the queue", "q");
    }

    public static EmbedBuilder createEmbed(MusicPlayer player, int page) {
        AudioTrack track = player.getPlayingTrack();
        List<AudioTrack> queue = player.getQueue();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(player.getGuild().getName());
        eb.appendDescription("**__Now Playing__:**\n");
        if (track != null) {
            AudioTrackInfo info = track.getInfo();
            eb.appendDescription("[" + info.title + "](" + info.uri + ") by " + info.author + "\n")
                .appendDescription(VPMUtil.getProgressBar(track))
                .appendDescription(" ")
                .appendDescription(Util.toString(track.getPosition()))
                .appendDescription("/")
                .appendDescription(Util.toString(track.getDuration()))
                .appendDescription("\n\n**__Up Next__:**\n");
            if (queue.isEmpty()) {
                eb.appendDescription("Emptiness, my old friend~");
            } else {
                eb.appendDescription(VPMUtil.listTracks(queue, page, 10, false))
                    .appendDescription("\n\nQueue Length: ")
                    .appendDescription(Util.toString(queue.stream().mapToLong(AudioTrack::getDuration).sum()));
            }
        } else {
            eb.appendDescription("Nothin' playin' in 'ere. Party's o'er. Let's 'ave an after-party whaddaya think?");
        }
        eb.setFooter((queue.isEmpty() ? "" : "Page " + (page + 1) + "/" + ((int) Math.ceil(queue.size() / 10.0)) + " | ")
            + "Loop: " + (player.isLoop() ? VPMUtil.CHECK_MARK : VPMUtil.CROSS_MARK)
            + " | Queue Loop: " + (player.isLoopQueue() ? VPMUtil.CHECK_MARK : VPMUtil.CROSS_MARK));
        return eb;
    }

    public static ActionRow createButtons(int page) {
        return ActionRow.of(
            Button.primary("queue:" + (page - 1), Emoji.fromUnicode("\u25C0")), // ◀ (Previous)
            Button.primary("queue:" + page, Emoji.fromUnicode("\uD83D\uDD04")), // 🔄 (Refresh)
            Button.primary("queue:" + (page + 1), Emoji.fromUnicode("\u25B6")) // ▶ (Next))
        );
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        e.sendEmbeds(createEmbed(PlayerManager.getPlayer(e), 0).build())
            .setActionRows(createButtons(0))
            .queue();
    }

    public static class QueueHandler implements ButtonHandler {
        @Override
        public String getName() {
            return "queue";
        }

        @Override
        public void handle(BotButtonEvent e) {
            Guild guild = e.getGuild();
            MusicPlayer player = PlayerManager.getPlayer(e.getBot(), guild);
            LinkedList<AudioTrack> queue = player.getQueue();
            int page = Math.max(0, Math.min((int) Math.ceil(queue.size() / 10.0) - 1, VPMUtil.toInt(e.getArg(0))));
            e.getInteraction()
                .editMessageEmbeds(createEmbed(player, page).build())
                .setActionRows(createButtons(page))
                .queue();
        }
    }
}
