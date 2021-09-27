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
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.vplaygames.vpm.player.MusicPlayer;
import net.vplaygames.vpm.player.PlayerManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Util {
    public static String[][] progressBarEmotes = {
        {
            "",
            "",
            "",
            ""
        },
        {
            "",
            "",
            "",
            "",
            ""
        },
        {
            "",
            "",
            "",
            ""
        }
    };

    private Util() {
        // Utility Class
    }

    public static String getProgressBar(long progress, long total, int bars) {
        int percent = (int) Math.ceil(progress * (bars * 3 + 5.0) / total);
        StringBuilder tor = new StringBuilder();
        for (int i = 0; i < progressBarEmotes.length; i++) {
            String[] bar = progressBarEmotes[i];
            if (progress > bar.length - 1) {

            }
            if (i == 1 && bars > 1) {
                i--;
                bars--;
            }
        }
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
                    tor.append(progressBarEmotes[0][0]);
                    break;
                case 1:
                    percent -= 1;
                    tor.append(progressBarEmotes[0][1]);
                    break;
                case 2:
                    percent -= 2;
                    tor.append(progressBarEmotes[0][2]);
                    break;
                case 3:
                    percent -= 3;
                    tor.append(progressBarEmotes[0][3]);
                    break;
                default:
                    percent -= 3;
                    tor.append(progressBarEmotes[0][4]);
                    break;
            }
        }
        switch (percent) {
            case 0:
                tor.append(progressBarEmotes[0][0]);
                break;
            case 1:
                percent -= 1;
                tor.append(progressBarEmotes[0][1]);
                break;
            case 2:
                percent -= 2;
                tor.append(progressBarEmotes[0][2]);
                break;
            case 3:
                percent -= 3;
                tor.append(progressBarEmotes[0][3]);
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
        e.send("Connected to " + targetVC.getAsMention()).queue();
        MusicPlayer player = PlayerManager.getInstance().getPlayer(e.getGuild());
        if (!e.getChannel().equals(player.getBoundChannel())) {
            e.send("Bound to " + ((GuildChannel) e.getChannel()).getAsMention()).queue();
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
        return "`" + info.title + "` by `" + info.author + "` Link: <" + info.uri + "> (" + toString(info.length) + ")";
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

    public static String repeat(char c, int count) {
        StringBuilder tor = new StringBuilder();
        while (count-- > 0) tor.append(c);
        return tor.toString();
    }

    public static String dateTimeNow() {
        String lt = LocalTime.now().toString();
        lt = lt.substring(0, lt.length() - 4);
        if (Util.toInt(lt.substring(0, 2)) > 12)
            lt = Util.toInt(lt.substring(0, 2)) - 12 + lt.substring(2) + " PM";
        else
            lt += " AM";
        return "on " + LocalDate.now().toString() + " at " + lt + " (" + TimeZone.getDefault().getDisplayName(false, 0) + ")";
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

    public static String toProperCase(String a) {
        String[] b = a.split(" ");
        for (int i = 0; i < b.length; i++)
            b[i] = b[i].toUpperCase().charAt(0) + b[i].substring(1).toLowerCase();
        return String.join(" ", b);
    }

    public static int toInt(String a) {
        int tor = 0;
        for (int i = 0; i < a.length(); i++) {
            char c = a.charAt(i);
            if ('0' <= c && c <= '9') {
                tor = tor * 10 + c - '0';
            }
        }
        return tor;
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
