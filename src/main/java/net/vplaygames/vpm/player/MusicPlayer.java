package net.vplaygames.vpm.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.*;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.vplaygames.vpm.core.Bot;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.Sender;
import net.vplaygames.vpm.core.Util;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class MusicPlayer extends DefaultAudioPlayer implements AudioEventListenerAdapter, AudioSendHandler, AudioLoadResultHandler {
    long guildId;
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

    public MusicPlayer() {
        super(PlayerManager.getInstance());
        addListener(this);
        frame.setBuffer(buffer);
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
        Bot.getShardManager().getGuildById(guildId).getAudioManager().setSendingHandler(this);
        return this;
    }

    @Override
    public void destroy() {
        super.destroy();
        Bot.getShardManager().getGuildById(guildId).getAudioManager().closeAudioConnection();
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
        VoiceChannel vc = getConnectedVC();
        if (vc == null) {
            return false;
        }
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

    public VoiceChannel getConnectedVC() {
        return Bot.getShardManager()
            .getGuildById(guildId)
            .getMember(Bot.getPrimaryShard().getSelfUser())
            .getVoiceState()
            .getChannel();
    }

    public TextChannel getBoundChannel() {
        return Bot.getShardManager().getTextChannelById(boundChannelId);
    }

    public void setBoundChannel(long id) {
        boundChannelId = id;
    }

    public void playNext() {
        clearSkipVotes();
        startTrack(queue.poll(), false);
    }

    public void queue(Sender e, AudioTrack track) {
        if (!startTrack(track, true)) {
            queue.offer(track);
            e.send("Added to queue: " + Util.toString(track)).queue();
        } else {
            e.send("Playing: " + Util.toString(track)).queue();
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
        return (ByteBuffer) buffer.flip();
    }

    @Override
    public boolean isOpus() {
        return true;
    }

    // Methods for loading search results
    public void loadAndPlay(String trackUrl, CommandReceivedEvent event, boolean isSearched) {
        this.isSearched = isSearched;
        this.event = event;
        PlayerManager.getInstance().loadItemOrdered(this, trackUrl, this);
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
            : "Something broke while playing the track!").queue();
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
                playNext();
            }
        } else if (e.endReason != AudioTrackEndReason.CLEANUP && loopQueue.get()) {
            queue.offer(getPlayingTrack().makeClone());
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
        getBoundChannel().sendMessage("Cannot play the following track:\n" + Util.toString(e.track)).queue();
    }
}
