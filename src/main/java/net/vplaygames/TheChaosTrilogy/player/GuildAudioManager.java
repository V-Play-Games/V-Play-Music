package net.vplaygames.TheChaosTrilogy.player;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.*;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Sender;
import net.vplaygames.TheChaosTrilogy.core.Util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class GuildAudioManager extends DefaultAudioPlayer implements AudioEventListener, AudioSendHandler {
    JDA jda;
    long guildId;
    ByteBuffer buffer;
    MutableAudioFrame frame;
    List<Member> skipVotes;
    Queue<AudioTrack> queue;
    AtomicBoolean loop;
    AtomicBoolean loopQueue;
    int listeningMemberCount;

    public GuildAudioManager(PlayerManager manager) {
        super(manager);
        this.queue = new LinkedBlockingQueue<>();
        this.addListener(this);
        this.skipVotes = new ArrayList<>();
        this.buffer = ByteBuffer.allocate(1024);
        this.frame = new MutableAudioFrame();
        this.frame.setBuffer(buffer);
        this.loop = new AtomicBoolean(false);
        this.loopQueue = new AtomicBoolean(true);
    }

    public boolean isLoop() {
        return loop.get();
    }

    public void setLoop(boolean loop) {
        this.loop.getAndSet(loop);
    }

    public boolean isLoopQueue() {
        return loopQueue.get();
    }

    public void setLoopQueue(boolean loopQueue) {
        this.loopQueue.set(loopQueue);
    }

    public GuildAudioManager setAudioManager(AudioManager audioManager) {
        this.jda = audioManager.getJDA();
        this.guildId = audioManager.getGuild().getIdLong();
        audioManager.setSendingHandler(this);
        return this;
    }

    public void disconnect() {
        destroy();
        jda.getGuildById(guildId).getAudioManager().closeAudioConnection();
        queue.clear();
    }

    public void toggleLoop() {
        setLoop(!loop.get());
    }

    public void toggleLoopQueue() {
        setLoopQueue(!loopQueue.get());
    }

    public Queue<AudioTrack> getQueue() {
        return queue;
    }

    public void skip(CommandReceivedEvent e) {
        if (skipVotes.contains(e.getMember())) {
            if (!checkSkip()) {
                e.send("You have already voted to skip! Current votes: ")
                    .append(skipVotes.size() + "/" + (listeningMemberCount - 1))
                    .queue();
            }
        }
        skipVotes.add(e.getMember());
        if (checkSkip()) {
            e.send("Successfully skipped!").queue();
        } else {
            e.send("You have voted to skip! Current votes: ")
                .append(skipVotes.size() + "/" + (listeningMemberCount - 1))
                .queue();
        }
    }

    public boolean checkSkip() {
        VoiceChannel vc = getConnectedVoiceChannel();
        if (vc == null) {
            return false;
        }
        if (skipVotes.isEmpty()) {
            return false;
        }
        List<Member> listeningMembers = Util.getListeningMembers(vc);
        if (listeningMembers.size() > 2 || skipVotes.size() != 1) {
            listeningMemberCount = listeningMembers.size();
            listeningMembers.removeAll(skipVotes);
            if (listeningMembers.size() > 1) {
                return false;
            }
        }
        playNext();
        return true;
    }

    public List<Member> getSkipVotes() {
        return skipVotes;
    }

    public VoiceChannel getConnectedVoiceChannel() {
        return jda.getGuildById(guildId).getMember(jda.getSelfUser()).getVoiceState().getChannel();
    }

    public void onPlayerPause() {
        // Unimplemented Method
    }

    public void onPlayerResume() {
        // Unimplemented Method
    }

    public void onTrackStart(AudioTrack track) {
        // Unimplemented Method
    }

    public void onTrackException(AudioTrack track, FriendlyException exception) {
        playNext();
    }

    public void onTrackStuck(AudioTrack track, long thresholdMs) {
        playNext();
    }

    public void onTrackEnd(AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            if (loop.get()) {
                startTrack(track.makeClone(), false);
            } else {
                playNext();
            }
        } else if (endReason != AudioTrackEndReason.CLEANUP && loopQueue.get()) {
            queue.offer(getPlayingTrack().makeClone());
        }
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

    @Override
    public void onEvent(AudioEvent event) {
        if (event instanceof PlayerPauseEvent) {
            onPlayerPause();
        } else if (event instanceof PlayerResumeEvent) {
            onPlayerResume();
        } else if (event instanceof TrackStartEvent) {
            onTrackStart(((TrackStartEvent) event).track);
        } else if (event instanceof TrackEndEvent) {
            onTrackEnd(((TrackEndEvent) event).track, ((TrackEndEvent) event).endReason);
        } else if (event instanceof TrackExceptionEvent) {
            onTrackException(((TrackExceptionEvent) event).track, ((TrackExceptionEvent) event).exception);
        } else if (event instanceof TrackStuckEvent) {
            TrackStuckEvent stuck = (TrackStuckEvent) event;
            onTrackStuck(stuck.track, stuck.thresholdMs);
        }
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
}
