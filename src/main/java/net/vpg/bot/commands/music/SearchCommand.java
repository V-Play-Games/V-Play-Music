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

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.vpg.bot.core.VPMUtil;
import net.vpg.bot.framework.Bot;
import net.vpg.bot.framework.BotButtonEvent;
import net.vpg.bot.framework.ButtonHandler;
import net.vpg.bot.framework.Sender;
import net.vpg.bot.framework.commands.BotCommandImpl;
import net.vpg.bot.framework.commands.CommandReceivedEvent;
import net.vpg.bot.player.PlayerManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchCommand extends BotCommandImpl {
    static final Map<String, List<AudioTrack>> searchResults = new HashMap<>();

    public SearchCommand(Bot bot) {
        super(bot, "search", "Searches a track");
        addOption(OptionType.STRING, "track", "Type the name of the song you want to search", true);
        setMinArgs(1);
    }

    public static EmbedBuilder createEmbed(List<AudioTrack> results, int page) {
        return createEmbed(results, page, -1);
    }

    public static EmbedBuilder createEmbed(List<AudioTrack> results, int page, int choice) {
        EmbedBuilder eb = new EmbedBuilder();
        String[] tracks = VPMUtil.listTracks(results, page, 5, true).split("\n");
        if (0 <= choice && choice < tracks.length)
            for (int i = 0; i < tracks.length; i++) {
                if (i != choice) {
                    tracks[i] = MarkdownUtil.strike(tracks[i]);
                } else {
                    tracks[i] = MarkdownUtil.bold(tracks[i]);
                }
            }
        eb.setTitle("Search Results")
            .appendDescription(String.join("\n", tracks))
            .setFooter("Page " + (page + 1) + "/" + ((int) Math.ceil(results.size() / 5.0)));
        return eb;
    }

    public static ActionRow[] createRows(List<AudioTrack> results, String userId, String resultId, int page) {
        String prefix = "search:" + userId + ":" + resultId + ":" + page + ":";
        int remaining = results.size() - (page * 5);
        return new ActionRow[]{
            ActionRow.of(
                // Choose 0 to 4 (0-based)
                Button.primary(prefix + "c:0", Emoji.fromUnicode("\u0031\u20E3")).withDisabled(remaining <= 0),
                Button.primary(prefix + "c:1", Emoji.fromUnicode("\u0032\u20E3")).withDisabled(remaining <= 1),
                Button.primary(prefix + "c:2", Emoji.fromUnicode("\u0033\u20E3")).withDisabled(remaining <= 2),
                Button.primary(prefix + "c:3", Emoji.fromUnicode("\u0034\u20E3")).withDisabled(remaining <= 3),
                Button.primary(prefix + "c:4", Emoji.fromUnicode("\u0035\u20E3")).withDisabled(remaining <= 4)
            ),
            ActionRow.of(
                Button.primary(prefix + "f", Emoji.fromUnicode("\u23E9")), // First Page
                Button.primary(prefix + "p", Emoji.fromUnicode("\u25C0")), // Previous
                Button.primary(prefix + "x", Emoji.fromUnicode(VPMUtil.CROSS_MARK)), // Cancel
                Button.primary(prefix + "n", Emoji.fromUnicode("\u25B6")), // Next
                Button.primary(prefix + "l", Emoji.fromUnicode("\u23EA")) // Last Page
            )
        };
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e, String.join(" ", e.getArgsFrom(1)));
    }

    @Override
    public void onSlashCommandRun(CommandReceivedEvent e) {
        execute(e, e.getString("track"));
    }

    public void execute(CommandReceivedEvent e, String track) {
        if (!VPMUtil.canJoinVC(e)) {
            return;
        }
        String id = e.getGuild().getId() + "-" + System.currentTimeMillis();
        PlayerManager.getManager(bot).loadItem("ytsearch:" + track, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                e.send("Only one result found\n" + VPMUtil.toString(track)).queue();
                PlayerManager.getPlayer(e).trackLoaded(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                searchResults.put(id, playlist.getTracks());
                e.sendEmbeds(createEmbed(playlist.getTracks(), 0).build())
                    .setActionRows(createRows(playlist.getTracks(), e.getUser().getId(), id, 0))
                    .queue();
            }

            @Override
            public void noMatches() {
                e.send("No Results Found").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                e.send(exception.severity == FriendlyException.Severity.COMMON
                    ? exception.getMessage()
                    : "Something broke while searching the track!").queue();
            }
        });
    }

    public static class SearchHandler implements ButtonHandler {
        @Override
        public String getName() {
            return "search";
        }

        @Override
        public void handle(BotButtonEvent e) {
            if (!e.getUser().getId().equals(e.getArg(0))) return;
            String id = e.getArg(1);
            List<AudioTrack> results = searchResults.get(id);
            if (results == null) {
                e.reply("The search option has already been chosen, please search again.").setEphemeral(true).queue();
                return;
            }
            int page = VPMUtil.toInt(e.getArg(2));
            switch (e.getArg(3)) {
                case "c":
                    int choice = VPMUtil.toInt(e.getArg(4));
                    AudioTrack track = results.get(page * 5 + choice);
                    //noinspection ConstantConditions
                    PlayerManager.getPlayer(e.getBot(), e.getGuild()).queue(Sender.fromMessage(e.getMessage()), track);

                    e.editComponents()
                        .setContent("Selected " + VPMUtil.toString(track))
                        .setEmbeds(createEmbed(results, page, choice).build())
                        .queue();
                    return;
                case "x":
                    e.editComponents()
                        .setContent("Search Cancelled")
                        .setEmbeds()
                        .queue();
                    return;
                case "f":
                    page = 0;
                    break;
                case "p":
                    page = Math.max(0, page + 1);
                    break;
                case "n":
                    page = Math.min(page + 1, (int) Math.floor(results.size() / 5.0));
                    break;
                case "l":
                    page = (int) Math.floor(results.size() / 5.0);
                    break;
                default:
                    e.reply("Failed to perform action, please contact VPG").queue();
                    return;
            }
            e.editComponents(createRows(results, e.getUser().getId(), id, page))
                .setEmbeds(createEmbed(results, page).build())
                .queue();
        }
    }
}
