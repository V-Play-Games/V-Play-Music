package net.vplaygames.vpm.core;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public interface ButtonHandler {
    String getName();

    boolean isValidClick(ButtonClickEvent e, String[] args);

    void handle(ButtonClickEvent e, String[] args);
}
