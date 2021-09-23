package net.vplaygames.vpm.player;

import com.sedmelluq.discord.lavaplayer.player.event.*;

public interface AudioEventListenerAdapter extends AudioEventListener {
    @Override
    default void onEvent(AudioEvent event) {
        if (event instanceof PlayerPauseEvent)
            onPlayerPause((PlayerPauseEvent) event);
        else if (event instanceof PlayerResumeEvent)
            onPlayerResume((PlayerResumeEvent) event);
        else if (event instanceof TrackStartEvent)
            onTrackStart((TrackStartEvent) event);
        else if (event instanceof TrackEndEvent)
            onTrackEnd((TrackEndEvent) event);
        else if (event instanceof TrackExceptionEvent)
            onTrackException((TrackExceptionEvent) event);
        else if (event instanceof TrackStuckEvent)
            onTrackStuck((TrackStuckEvent) event);
    }

    default void onPlayerPause(PlayerPauseEvent event) {
        // dummy method for implementation
    }

    default void onPlayerResume(PlayerResumeEvent event) {
        // dummy method for implementation
    }

    default void onTrackStart(TrackStartEvent event) {
        // dummy method for implementation
    }

    default void onTrackEnd(TrackEndEvent event) {
        // dummy method for implementation
    }

    default void onTrackException(TrackExceptionEvent event) {
        // dummy method for implementation
    }

    default void onTrackStuck(TrackStuckEvent event) {
        // dummy method for implementation
    }
}
