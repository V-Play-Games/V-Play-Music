package net.vplaygames.TheChaosTrilogy.core;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public interface ActionHandler {
    String getName();

    void handle(ButtonClickEvent e, String arg);

    class Gender implements ActionHandler {
        @Override
        public String getName() {
            return "gender";
        }

        @Override
        public void handle(ButtonClickEvent e, String arg) {
            Player player = Bot.players.get(e.getUser().getIdLong());
            if (player.getMale() == -1) {
                player.setMale(arg.equals("m") ? 1 : 0);
            }
        }
    }

    class Area implements ActionHandler {
        @Override
        public String getName() {
            return "area";
        }

        @Override
        public void handle(ButtonClickEvent e, String arg) {
            Bot.dialogueMap.get(arg).send(e);
        }
    }

    class Starter implements ActionHandler {
        @Override
        public String getName() {
            return "starter";
        }

        @Override
        public void handle(ButtonClickEvent e, String arg) {
        }
    }

    class Property implements ActionHandler {
        @Override
        public String getName() {
            return "property";
        }

        @Override
        public void handle(ButtonClickEvent e, String arg) {
            Player player = Bot.getPlayer(e.getUser().getIdLong());
            String[] args = arg.split(";");
            player.setProperty(args[0], args[1]);
        }
    }
}
