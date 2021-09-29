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
package net.vplaygames.vpm.core;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.vplaygames.vpm.player.MusicPlayer;
import net.vplaygames.vpm.player.PlayerManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Util {
    public static final String CROSS_MARK = "\u274C";
    public static final String TICK_MARK = "\u2705";
    public static final String[][] progressBarEmotes = {
        {
            "<:PB01:892072629899505684>",
            "<:PB02:892072630461530122>",
            "<:PB03:892072623519969332>"
        },
        {
            "<:PB10:892072617664712704>",
            "<:PB11:892072617832509480>",
            "<:PB12:892072617077530634>",
            "<:PB13:892072616268034059>",
            "<:PB14:892072617069146192>"
        },
        {
            "<:PB20:892072617962508348>",
            "<:PB21:892072627043196999>",
            "<:PB22:892072627005440040>",
            "<:PB23:892072618176426074> "
        }
    };
    public static Pattern DELIMITER = Pattern.compile("[\n\\s]");

    private Util() {
        // Utility Class
    }

    public static String getProgressBar(AudioTrack track, int bars) {
        return getProgressBar(track.getPosition(), track.getDuration(), bars);
    }

    public static String getProgressBar(long progress, long total, int bars) {
        int percent = (int) Math.ceil(progress * (bars * 3 + 5.0) / total);
        StringBuilder tor = new StringBuilder();
        switch (percent) {
            case 0:
                // Shouldn't happen
                throw new InternalError();
            case 1:
                percent -= 1;
                tor.append(progressBarEmotes[0][0]);
                break;
            case 2:
                percent -= 2;
                tor.append(progressBarEmotes[0][1]);
                break;
            default:
                percent -= 2;
                tor.append(progressBarEmotes[0][2]);
                break;
        }
        for (int i = 1; i <= bars; i++) {
            switch (percent) {
                case 0:
                    tor.append(progressBarEmotes[1][0]);
                    break;
                case 1:
                    percent -= 1;
                    tor.append(progressBarEmotes[1][1]);
                    break;
                case 2:
                    percent -= 2;
                    tor.append(progressBarEmotes[1][2]);
                    break;
                case 3:
                    percent -= 3;
                    tor.append(progressBarEmotes[1][3]);
                    break;
                default:
                    percent -= 3;
                    tor.append(progressBarEmotes[1][4]);
                    break;
            }
        }
        switch (percent) {
            case 0:
                tor.append(progressBarEmotes[2][0]);
                break;
            case 1:
                percent -= 1;
                tor.append(progressBarEmotes[2][1]);
                break;
            case 2:
                percent -= 2;
                tor.append(progressBarEmotes[2][2]);
                break;
            case 3:
                percent -= 3;
                tor.append(progressBarEmotes[2][3]);
                break;
            default:
                // Shouldn't happen
                throw new InternalError();
        }
        assert percent == 0;
        return tor.toString();
    }

    public static boolean canJoinVC(CommandReceivedEvent e) {
        VoiceChannel targetVC = e.getMember().getVoiceState().getChannel();
        if (targetVC == null) {
            e.send("Sussy Baka, You have to be in a voice channel for this command to work smh.").queue();
            return false;
        }
        if (e.getMember().getVoiceState().isDeafened()) {
            e.send("You can't use this command while being deafened!").queue();
            return false;
        }
        VoiceChannel selfVC = e.getSelfMember().getVoiceState().getChannel();
        if (targetVC.equals(selfVC)) {
            return true;
        }
        if (selfVC != null && getListeningMembers(selfVC).isEmpty()) {
            e.send("I'm currently vibin' with my people in ")
                .append(selfVC.getAsMention())
                .append("\nC'mere or get lost :)").queue();
            return false;
        }
        if (!e.getSelfMember().hasAccess(targetVC)) {
            e.send("I am not stronk enough to join ")
                .append(targetVC.getAsMention())
                .append("\n(Insufficient Permissions: Make sure the bot has View Channel and Connect permissions)").queue();
            return false;
        }
        e.getGuild().getAudioManager().openAudioConnection(targetVC);
        e.send("Connected to " + targetVC.getAsMention()).append("\n");
        MusicPlayer player = PlayerManager.getPlayer(e.getGuild());
        if (!e.getChannel().equals(player.getBoundChannel())) {
            e.send("Bound to " + ((GuildChannel) e.getChannel()).getAsMention()).append("\n");
            player.setBoundChannel(e.getChannel().getIdLong());
        }
        return true;
    }

    public static boolean isUri(String uri) {
        try {
            new URI(uri);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public static String toString(AudioTrack track) {
        return toString(track.getInfo());
    }

    public static String toString(AudioTrackInfo info) {
        return "`" + info.title + "` by `" + info.author + "` (" + toString(info.length) + ")\n<" + info.uri + ">";
    }

    public static String toString(long ms) {
        ms /= 1000;
        long hr = ms / 3600;
        long min = (ms % 3600) / 60;
        long sec = ms % 60;
        return (hr == 0 ? "" : (hr < 10 ? "0" : "") + hr + ":") + (min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec;
    }

    public static List<Member> getListeningMembers(VoiceChannel vc) {
        return vc.getMembers()
            .stream()
            .filter(member -> !(member.getUser().isBot() || member.getVoiceState().isDeafened()))
            .collect(Collectors.toList());
    }

    public static File makeFileOf(Object toBeWritten, String fileName) {
        File tor = new File(fileName);
        try (PrintStream stream = new PrintStream(tor)) {
            stream.println(toBeWritten);
        } catch (FileNotFoundException e) {
            // ignore
        }
        tor.deleteOnExit();
        return tor;
    }

    public static String getString(SlashCommandEvent e, String name) {
        return getString(e, name, "");
    }

    public static String getString(SlashCommandEvent e, String name, String def) {
        return e.getOptions()
            .stream()
            .filter(opt -> opt.getName().equals(name))
            .findFirst()
            .map(OptionMapping::getAsString)
            .orElse(def);
    }

    public static String getMethod(String s) {
        return s.substring(0, s.indexOf(':'));
    }

    public static String getArgs(String s) {
        return s.substring(s.indexOf(':') + 1);
    }

    public static String toProperCase(String s) {
        boolean capitalNextLetter = true;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLetter(c))
                if (capitalNextLetter)
                    sb.append(Character.toUpperCase(c));
                else
                    sb.append(Character.toLowerCase(c));
            else
                sb.append(c);
            capitalNextLetter = DELIMITER.matcher(Character.toString(c)).matches();
        }
        return sb.toString();
    }

    public static int toInt(String s) {
        int sign = s.charAt(0) == '-' ? -1 : +1;
        int tor = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ('0' <= c && c <= '9') {
                tor = tor * 10 + c - '0';
            }
        }
        return tor * sign;
    }

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
            if (queue.size() != 0) {
                eb.appendDescription(listTracks(queue, page, 10))
                    .appendDescription("\n\nQueue size: ")
                    .appendDescription(toString(queue.stream().mapToLong(AudioTrack::getDuration).sum()));
            } else {
                eb.appendDescription("*cricket cricket*");
            }
        } else {
            eb.appendDescription("Nothin' playin' in 'ere. Party's o'er. Let's 'ave an after-party whaddaya think?");
        }
        eb.setFooter("Page " + (page + 1) + "/" + ((int) Math.ceil(queue.size() / 10.0))
            + " | Loop: " + (player.isLoop() ? TICK_MARK : CROSS_MARK)
            + " | Queue Loop: " + (player.isLoopQueue() ? TICK_MARK : CROSS_MARK));
        return eb;
    }

    public static ActionRow createButtons(int page) {
        return ActionRow.of(
            Button.primary("queue:" + (page - 1), Emoji.fromUnicode("\u25C0")), // ◀ (Previous)
            Button.primary("queue:" + page, Emoji.fromUnicode("\uD83D\uDD04")), // 🔄 (Refresh)
            Button.primary("queue:" + (page + 1), Emoji.fromUnicode("\u25B6")) // ▶ (Next))
        );
    }

    public static String listTracks(List<AudioTrack> queue, int page, int limit) {
        AtomicInteger i = new AtomicInteger(page * 10);
        return queue.stream()
            .skip(page * limit)
            .limit(limit)
            .map(AudioTrack::getInfo)
            .map(info -> i.incrementAndGet() + ". [" + info.title + "](" + info.uri + ") by " + info.author + " (" + toString(info.length) + ")")
            .collect(Collectors.joining("\n"));
    }

    public static boolean equalsAnyIgnoreCase(String b, String... a) {
        for (String s : a) if (b.equalsIgnoreCase(s)) return true;
        return false;
    }

    public static String reduceToAlphabets(String s) {
        StringBuilder tor = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isAlphabetic(c)) {
                tor.append(c);
            }
        }
        return tor.toString();
    }

    public static String replaceAll(Pattern pattern, String text, Function<MatchResult, String> replaceStringGetter) {
        Matcher m = pattern.matcher(text);
        while (m.find()) {
            text = text.replace(m.group(), replaceStringGetter.apply(m.toMatchResult()));
        }
        return text;
    }

    @SafeVarargs
    public static <T> List<T> list(T... elements) {
        return Arrays.stream(elements).collect(Collectors.toList());
    }
}
