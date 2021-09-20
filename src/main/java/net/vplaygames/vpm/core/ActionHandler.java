package net.vplaygames.vpm.core;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public interface ActionHandler {
    String getName();

    void handle(ButtonClickEvent e, String arg);
}
