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
package net.vpg.bot.core;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.vpg.bot.framework.Util;
import net.vpg.bot.framework.commands.CommandReceivedEvent;
import net.vpg.bot.framework.commands.CommandReplyAction;
import net.vpg.bot.player.MusicPlayer;
import net.vpg.bot.player.PlayerManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class VPMUtil {
    public static final String CROSS_MARK = "\u274C";
    public static final String CHECK_MARK = "\u2705";
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

    private VPMUtil() {
        // Utility Class
    }

    public static String getProgressBar(AudioTrack track, int bars) {
        return getProgressBar(track.getPosition(), track.getDuration(), bars);
    }

    public static String getProgressBar(long progress, long total, int bars) {
        int percent = (int) Math.ceil(progress * (bars * 3 + 5.0) / total);
        StringBuilder tor = new StringBuilder();
        switch (percent) {
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
                tor.append(progressBarEmotes[2][1]);
                break;
            case 2:
                tor.append(progressBarEmotes[2][2]);
                break;
            case 3:
                tor.append(progressBarEmotes[2][3]);
                break;
        }
        return tor.toString();
    }

    @SuppressWarnings("ConstantConditions")
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
        CommandReplyAction action = e.send("Connected to " + targetVC.getAsMention()).append("\n");
        MusicPlayer player = PlayerManager.getPlayer(e);
        if (!e.getChannel().equals(player.getBoundChannel())) {
            //noinspection ResultOfMethodCallIgnored
            action.append("Bound to ").append(((GuildChannel) e.getChannel()).getAsMention()).append("\n");
            player.setBoundChannel(e.getChannel().getIdLong());
        }
        action.queue();
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
        return String.format("`%s` by `%s` (%s)\n<%s>", info.title, info.author, Util.toString(info.length), info.uri);
    }

    public static List<Member> getListeningMembers(VoiceChannel vc) {
        //noinspection ConstantConditions
        return vc.getMembers()
            .stream()
            .filter(member -> !(member.getUser().isBot() || member.getVoiceState().isDeafened()))
            .collect(Collectors.toList());
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

    public static String listTracks(List<AudioTrack> queue, int page, int limit, boolean startWithOne) {
        AtomicInteger i = new AtomicInteger(page * limit * (startWithOne ? 0 : 1));
        return queue.stream()
            .skip(page * limit)
            .limit(limit)
            .map(AudioTrack::getInfo)
            .map(info -> String.format("%d. [%s](%s) by %s (%s)", i.incrementAndGet(), info.title, info.uri, info.author, Util.toString(info.length)))
            .collect(Collectors.joining("\n"));
    }
}
