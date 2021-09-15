package net.vplaygames.TheChaosTrilogy.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;

public class TrackLoadResultHandler implements AudioLoadResultHandler {
    GuildAudioManager manager;
    CommandReceivedEvent event;
    String trackUrl;

    public TrackLoadResultHandler(CommandReceivedEvent event, String trackUrl, GuildAudioManager manager) {
        this.event = event;
        this.trackUrl = trackUrl;
        this.manager = manager;
    }

    public static void load(CommandReceivedEvent event, String trackUrl, GuildAudioManager manager) {
        PlayerManager.getInstance()
            .loadItemOrdered(manager, trackUrl, new TrackLoadResultHandler(event, trackUrl, manager));
    }

    public CommandReceivedEvent getEvent() {
        return event;
    }

    public String getTrackUrl() {
        return trackUrl;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        manager.queue(event, track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if (playlist.isSearchResult()) {
            trackLoaded(playlist.getTracks().get(0));
        } else {
            manager.queue(event, playlist);
        }
    }

    @Override
    public void noMatches() {

    }

    @Override
    public void loadFailed(FriendlyException exception) {

    }
}
