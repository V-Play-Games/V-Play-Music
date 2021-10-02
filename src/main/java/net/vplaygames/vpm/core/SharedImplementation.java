package net.vplaygames.vpm.core;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.vplaygames.vpm.player.MusicPlayer;
import net.vplaygames.vpm.player.PlayerManager;

import java.util.List;

public class SharedImplementation {
    public static class Queue {
        public static EmbedBuilder createEmbed(Guild guild, int page) {
            MusicPlayer player = PlayerManager.getPlayer(guild);
            AudioTrack track = player.getPlayingTrack();
            List<AudioTrack> queue = player.getQueue();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(guild.getName());
            eb.appendDescription("**__Now Playing__:**\n");
            if (track != null) {
                AudioTrackInfo info = track.getInfo();
                eb.appendDescription("[" + info.title + "](" + info.uri + ") by " + info.author + "\n")
                    .appendDescription(Util.getProgressBar(track, 12))
                    .appendDescription(" ")
                    .appendDescription(Util.toString(track.getPosition()))
                    .appendDescription("/")
                    .appendDescription(Util.toString(track.getDuration()))
                    .appendDescription("\n\n**__Up Next__:**\n");
                if (queue.isEmpty()) {
                    eb.appendDescription("Emptiness, my old friend~");
                } else {
                    eb.appendDescription(Util.listTracks(queue, page, 10, false))
                        .appendDescription("\n\nQueue Length: ")
                        .appendDescription(Util.toString(queue.stream().mapToLong(AudioTrack::getDuration).sum()));
                }
            } else {
                eb.appendDescription("Nothin' playin' in 'ere. Party's o'er. Let's 'ave an after-party whaddaya think?");
            }
            eb.setFooter((queue.isEmpty() ? "" : "Page " + (page + 1) + "/" + ((int) Math.ceil(queue.size() / 10.0)) + " | ")
                + "Loop: " + (player.isLoop() ? Util.CHECK_MARK : Util.CROSS_MARK)
                + " | Queue Loop: " + (player.isLoopQueue() ? Util.CHECK_MARK : Util.CROSS_MARK));
            return eb;
        }

        public static ActionRow createButtons(int page) {
            return ActionRow.of(
                Button.primary("queue:" + (page - 1), Emoji.fromUnicode("\u25C0")), // ◀ (Previous)
                Button.primary("queue:" + page, Emoji.fromUnicode("\uD83D\uDD04")), // 🔄 (Refresh)
                Button.primary("queue:" + (page + 1), Emoji.fromUnicode("\u25B6")) // ▶ (Next))
            );
        }
    }

    public static class Search {
        public static EmbedBuilder createEmbed(List<AudioTrack> results, int page) {
            return createEmbed(results, page, -1);
        }

        public static EmbedBuilder createEmbed(List<AudioTrack> results, int page, int choice) {
            EmbedBuilder eb = new EmbedBuilder();
            String[] tracks = Util.listTracks(results, page, 5, true).split("\n");
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
                    Button.primary(prefix + "x", Emoji.fromUnicode(Util.CROSS_MARK)), // Cancel
                    Button.primary(prefix + "n", Emoji.fromUnicode("\u25B6")), // Next
                    Button.primary(prefix + "l", Emoji.fromUnicode("\u23EA")) // Last Page
                )
            };
        }
    }
}
