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
package net.vpg.bot.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.*;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.vpg.bot.action.Sender;
import net.vpg.bot.core.Bot;
import net.vpg.bot.core.VPMUtil;
import net.vpg.bot.event.CommandReceivedEvent;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class MusicPlayer extends DefaultAudioPlayer implements AudioEventListenerAdapter, AudioSendHandler, AudioLoadResultHandler {
    long guildId;
    long quizHostId;
    boolean quiz;
    CommandReceivedEvent event;
    boolean isSearched;
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    MutableAudioFrame frame = new MutableAudioFrame();
    Set<Long> skipVotes = new HashSet<>();
    LinkedList<AudioTrack> queue = new LinkedList<>();
    AtomicBoolean loop = new AtomicBoolean();
    AtomicBoolean loopQueue = new AtomicBoolean();
    int listeningMemberCount;
    long boundChannelId;
    PlayerManager manager;
    Bot bot;

    public MusicPlayer(PlayerManager manager) {
        super(manager);
        this.manager = manager;
        this.bot = manager.bot;
        addListener(this);
        frame.setBuffer(buffer);
    }

    public long getQuizHostId() {
        return quizHostId;
    }

    public void setQuizHostId(long quizHostId) {
        this.quizHostId = quizHostId;
    }

    public Guild getGuild() {
        return bot.getShardManager().getGuildById(guildId);
    }

    public boolean isLoop() {
        return loop.get();
    }

    public void setLoop(boolean loop) {
        this.loop.set(loop);
    }

    public void toggleLoop() {
        setLoop(!loop.get());
    }

    public boolean isLoopQueue() {
        return loopQueue.get();
    }

    public void setLoopQueue(boolean loopQueue) {
        this.loopQueue.set(loopQueue);
    }

    public void toggleLoopQueue() {
        setLoopQueue(!loopQueue.get());
    }

    public MusicPlayer configure(long guildId) {
        this.guildId = guildId;
        //noinspection ConstantConditions
        getGuild().getAudioManager().setSendingHandler(this);
        return this;
    }

    @Override
    public void destroy() {
        super.destroy();
        bot.getShardManager().getGuildById(guildId).getAudioManager().closeAudioConnection();
        setPaused(false);
        setLoop(false);
        setLoopQueue(false);
        skipVotes.clear();
        queue.clear();
    }

    public LinkedList<AudioTrack> getQueue() {
        return queue;
    }

    public void skip(CommandReceivedEvent e, List<Member> listeningMembers) {
        boolean added = skipVotes.add(e.getMember().getIdLong());
        if (checkSkip(listeningMembers)) {
            e.send("Successfully skipped!").queue();
        } else {
            e.send("You have " + (added ? "" : "already ") + "voted to skip! Current votes: ")
                .append(skipVotes.size() + "/" + (listeningMemberCount - 1))
                .queue();
        }
    }

    public boolean checkSkip(List<Member> listeningMembers) {
        listeningMemberCount = listeningMembers.size();
        if (listeningMemberCount > 2) {
            if (listeningMembers.stream()
                .filter(m -> !skipVotes.contains(m.getIdLong()))
                .count() > 1) {
                return false;
            }
        }
        playNext();
        return true;
    }

    public Set<Long> getSkipVotes() {
        return skipVotes;
    }

    public AudioChannel getConnectedAudioChannel() {
        return bot.getShardManager()
            .getGuildById(guildId)
            .getMember(bot.getPrimaryShard().getSelfUser())
            .getVoiceState()
            .getChannel();
    }

    public TextChannel getBoundChannel() {
        return getGuild().getTextChannelById(boundChannelId);
    }

    public void setBoundChannel(long id) {
        boundChannelId = id;
    }

    public void playNext() {
        clearSkipVotes();
        AudioTrack track = queue.poll();
        if (startTrack(track, false) && quizHostId != 0) {
            bot.getShardManager()
                .getUserById(quizHostId)
                .openPrivateChannel()
                .flatMap(c -> c.sendMessage(VPMUtil.toString(track)))
                .queue();
        }
    }

    public void queue(Sender e, AudioTrack track) {
        if (!startTrack(track, true)) {
            queue.offer(track);
            e.send("Added to queue: " + VPMUtil.toString(track)).setEphemeral(quiz).queue();
        } else {
            e.send("Playing: " + VPMUtil.toString(track)).setEphemeral(quiz).queue();
        }
    }

    public void queue(Sender e, AudioPlaylist playlist) {
        queue.addAll(playlist.getTracks());
        if (getPlayingTrack() == null) {
            playNext();
        }
        e.send("Added to queue: ")
            .append(Integer.toString(playlist.getTracks().size()))
            .append(" tracks from playlist `")
            .append(playlist.getName())
            .append("`")
            .setEphemeral(quiz)
            .queue();
    }

    public void clearSkipVotes() {
        skipVotes.clear();
    }

    // Methods for sending Audio
    @Override
    public boolean canProvide() {
        return provide(frame);
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return buffer.flip();
    }

    @Override
    public boolean isOpus() {
        return true;
    }

    // Methods for loading search results
    public void loadAndPlay(String trackUrl, CommandReceivedEvent event, boolean isSearched, boolean quiz) {
        this.isSearched = isSearched;
        this.event = event;
        this.quiz = quiz;
        manager.loadItemOrdered(this, trackUrl, this);
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        queue(event, track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if (isSearched) {
            queue(event, playlist.getTracks().get(0));
        } else {
            if (playlist.isSearchResult()) {
                queue(event, playlist.getTracks().get(0));
            } else {
                queue(event, playlist);
            }
        }
    }

    @Override
    public void noMatches() {
        event.send("Could not find any matches for that").queue();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        getBoundChannel().sendMessage(exception.severity == FriendlyException.Severity.COMMON
            ? exception.getMessage()
            : "Something broke while trying to play the track!").queue();
    }

    // Methods for processing events
    @Override
    public void onPlayerPause(PlayerPauseEvent e) {
        // Not implemented yet
    }

    @Override
    public void onPlayerResume(PlayerResumeEvent e) {
        // Not implemented yet
    }

    @Override
    public void onTrackStart(TrackStartEvent e) {
        // Not implemented yet
    }

    @Override
    public void onTrackEnd(TrackEndEvent e) {
        if (e.endReason.mayStartNext) {
            if (loop.get()) {
                startTrack(e.track.makeClone(), false);
            } else {
                if (quizHostId != 0) {
                    getBoundChannel().sendMessage("The song was " + VPMUtil.toString(e.track)).queue();
                }
                playNext();
            }
        } else if (e.endReason != AudioTrackEndReason.CLEANUP && loopQueue.get()) {
            queue.offer(e.track.makeClone());
        }
    }

    @Override
    public void onTrackException(TrackExceptionEvent e) {
        getBoundChannel().sendMessage(e.exception.severity == FriendlyException.Severity.COMMON
            ? e.exception.getMessage()
            : "Something broke while playing the track!").queue();
    }

    @Override
    public void onTrackStuck(TrackStuckEvent e) {
        getBoundChannel().sendMessage("Cannot play the following track:\n" + VPMUtil.toString(e.track)).queue();
    }
}
