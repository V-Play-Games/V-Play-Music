package net.vplaygames.vpm.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.vplaygames.vpm.core.CommandReceivedEvent;

public class TrackLoadResultHandler implements AudioLoadResultHandler {
    GuildAudioManager manager;
    CommandReceivedEvent event;
    boolean isSearched;

    public TrackLoadResultHandler(GuildAudioManager manager) {
        this.manager = manager;
    }

    public void load(String trackUrl, CommandReceivedEvent event, boolean isSearched) {
        this.isSearched = isSearched;
        this.event = event;
        PlayerManager.getInstance().loadItemOrdered(manager, trackUrl, this);
    }

    public CommandReceivedEvent getEvent() {
        return event;
    }

    public void setEvent(CommandReceivedEvent event) {
        this.event = event;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        manager.queue(event, track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if (isSearched) {
            trackLoaded(playlist.getTracks().get(0));
        } else {
            if (playlist.isSearchResult()) {
                trackLoaded(playlist.getTracks().get(0));
            } else {
                manager.queue(event, playlist);
            }
        }
    }

    @Override
    public void noMatches() {
        // Unimplemented Method
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        // Unimplemented Method
    }
}
