package net.vplaygames.vpm.core;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.vplaygames.vpm.player.PlayerManager;

import java.util.LinkedList;
import java.util.List;

public interface ButtonHandler {
    String getName();

    void handle(ButtonClickEvent e, String[] args);

    class QueueHandler implements ButtonHandler {
        @Override
        public String getName() {
            return "queue";
        }

        @Override
        public void handle(ButtonClickEvent e, String[] args) {
            Guild guild = e.getGuild();
            LinkedList<AudioTrack> queue = PlayerManager.getPlayer(guild).getQueue();
            int page = Math.max(0, Math.min((int) Math.ceil(queue.size() / 10.0) - 1, Util.toInt(args[0])));
            e.getInteraction()
                .editMessageEmbeds(SharedImplementation.Queue.createEmbed(guild, page).build())
                .setActionRows(SharedImplementation.Queue.createButtons(page))
                .queue();
        }
    }

    class SearchHandler implements ButtonHandler {
        @Override
        public String getName() {
            return "search";
        }

        @Override
        public void handle(ButtonClickEvent e, String[] args) {
            if (!e.getUser().getId().equals(args[0])) {
                return;
            }
            String id = args[1];
            List<AudioTrack> results = Bot.searchResults.get(id);
            if (results == null) {
                e.reply("The search option has already been chosen, please search again.").setEphemeral(true).queue();
                return;
            }
            int page = Util.toInt(args[2]);
            switch (args[3]) {
                case "c":
                    int choice = Util.toInt(args[4]);
                    AudioTrack track = results.get(page * 5 + choice);
                    PlayerManager.getPlayer(e.getGuild()).queue(Sender.fromMessage(e.getMessage()), track);

                    e.editComponents()
                        .setContent("Selected " + Util.toString(track))
                        .setEmbeds(SharedImplementation.Search.createEmbed(results, page, choice).build())
                        .queue();
                    return;
                case "x":
                    e.editComponents()
                        .setContent("Search Cancelled")
                        .setEmbeds()
                        .queue();
                    return;
                case "f":
                    page = 0;
                    break;
                case "p":
                    page = Math.max(0, page + 1);
                    break;
                case "n":
                    page = Math.min(page + 1, (int) Math.floor(results.size() / 5.0));
                    break;
                case "l":
                    page = (int) Math.floor(results.size() / 5.0);
                    break;
                default:
                    e.reply("Failed to perform action, please contact VPG").queue();
                    return;
            }
            e.editComponents(SharedImplementation.Search.createRows(results, e.getUser().getId(), id, page))
                .setEmbeds(SharedImplementation.Search.createEmbed(results, page).build())
                .queue();
        }
    }
}
