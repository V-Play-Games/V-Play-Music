package net.vplaygames.TheChaosTrilogy.player;

import com.sedmelluq.discord.lavaplayer.filter.PcmFilterFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GuildAudioManager extends AudioEventAdapter {
    JDA jda;
    long guildId;
    AudioPlayer player;
    AudioPlayerSendHandler handler;
    List<Member> skipVotes;
    Queue<AudioTrack> queue;
    int listeningMemberCount;
    public GuildAudioManager(PlayerManager playerManager, AudioManager audioManager) {
        this.jda = audioManager.getJDA();
        this.guildId = audioManager.getGuild().getIdLong();
        this.player = playerManager.createPlayer();
        this.queue = new LinkedBlockingQueue<>();
        player.addListener(this);
        this.handler = new AudioPlayerSendHandler(player);
        this.skipVotes = new ArrayList<>();
        audioManager.setSendingHandler(handler);
    }

    public Queue<AudioTrack> getQueue() {
        return queue;
    }

    public AudioTrack getPlayingTrack() {
        return player.getPlayingTrack();
    }

    public void playTrack(AudioTrack track) {
        player.playTrack(track);
    }

    public boolean startTrack(AudioTrack track, boolean noInterrupt) {
        return player.startTrack(track, noInterrupt);
    }

    public void stopTrack() {
        player.stopTrack();
    }

    public int getVolume() {
        return player.getVolume();
    }

    public void setVolume(int volume) {
        player.setVolume(volume);
    }

    public void setFilterFactory(PcmFilterFactory factory) {
        player.setFilterFactory(factory);
    }

    public void setFrameBufferDuration(Integer duration) {
        player.setFrameBufferDuration(duration);
    }

    public boolean isPaused() {
        return player.isPaused();
    }

    public void setPaused(boolean value) {
        player.setPaused(value);
    }

    public void destroy() {
        player.destroy();
    }

    public void addListener(AudioEventListener listener) {
        player.addListener(listener);
    }

    public void removeListener(AudioEventListener listener) {
        player.removeListener(listener);
    }

    public void checkCleanup(long threshold) {
        player.checkCleanup(threshold);
    }

    public AudioFrame provide() {
        return player.provide();
    }

    public AudioFrame provide(long timeout, TimeUnit unit) throws TimeoutException, InterruptedException {
        return player.provide(timeout, unit);
    }

    public boolean provide(MutableAudioFrame targetFrame) {
        return player.provide(targetFrame);
    }

    public boolean provide(MutableAudioFrame targetFrame, long timeout, TimeUnit unit) throws TimeoutException, InterruptedException {
        return player.provide(targetFrame, timeout, unit);
    }

//    public AudioPlayer getPlayer() {
//        return player;
//    }

    public AudioPlayerSendHandler getHandler() {
        return handler;
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
        if (skipVotes.size() == 0) {
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

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            playNext();
        }
    }

    public void playNext() {
        clearSkipVotes();
        player.startTrack(queue.poll(), false);
    }

    public void queue(CommandReceivedEvent e, AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
            e.send("Added to queue: " + Util.toString(track)).queue();
        } else {
            e.send("Playing: " + Util.toString(track)).queue();
        }
    }

    public void queue(CommandReceivedEvent e, AudioPlaylist playlist) {
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
}
