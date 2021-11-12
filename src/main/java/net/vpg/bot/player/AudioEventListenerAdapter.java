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
