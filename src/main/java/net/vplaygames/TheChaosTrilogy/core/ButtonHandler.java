package net.vplaygames.TheChaosTrilogy.core;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public interface ButtonHandler {
    String getName();

    boolean isValidClick(ButtonClickEvent e, String[] args);

    void handle(ButtonClickEvent e, String[] args);

    class Area implements ButtonHandler {
        @Override
        public String getName() {
            return "area";
        }

        @Override
        public boolean isValidClick(ButtonClickEvent e, String[] args) {
            return args[3].equals(e.getId());
        }

        @Override
        public void handle(ButtonClickEvent e, String[] args) {
            Bot.getDialogue(args[0]).executeActions(e, args[1], args[2]);
        }
    }
}
