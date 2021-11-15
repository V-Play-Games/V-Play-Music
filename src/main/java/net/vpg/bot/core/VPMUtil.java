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
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.vpg.bot.Driver;
import net.vpg.bot.framework.Util;
import net.vpg.bot.framework.commands.CommandReceivedEvent;
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
    public static final String[][] progressBar;

    static {
        progressBar = DataArray.fromJson(Driver.class.getResourceAsStream("emotes.json"))
            .stream(DataArray::getArray)
            .map(data -> data.stream(DataArray::getString).toArray(String[]::new))
            .toArray(String[][]::new);
    }

    private VPMUtil() {
        // Utility Class
    }

    public static String getProgressBar(AudioTrack track) {
        return getProgressBar(track, 12);
    }

    public static String getProgressBar(AudioTrack track, int bars) {
        return getProgressBar(track.getPosition(), track.getDuration(), bars);
    }

    public static String getProgressBar(long progress, long total, int bars) {
        int percent = (int) Math.ceil(progress * (bars * 3.0 + 5) / total);
        StringBuilder tor = new StringBuilder();
        for (int i = 0; i <= bars + 1; i++) {
            // 0 -> start; 0 < i <= bars -> mid; i > bars -> end
            String[] emotes = progressBar[i == 0 ? 0 : i > bars ? 2 : 1];
            switch (percent) {
                case 0:
                    tor.append(emotes[0]);
                    break;
                case 1:
                    percent -= 1;
                    tor.append(emotes[1]);
                    break;
                case 2:
                    percent -= 2;
                    tor.append(emotes[2]);
                    break;
                case 3:
                    percent -= 3;
                    tor.append(emotes[3]);
                    break;
                default:
                    percent -= emotes.length - 2;
                    tor.append(emotes[emotes.length - 1]);
                    break;
            }
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
        if (selfVC != null && !getListeningMembers(selfVC).isEmpty()) {
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
        MessageAction action = e.getChannel().sendMessage("Connected to " + targetVC.getAsMention()).append("\n");
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
