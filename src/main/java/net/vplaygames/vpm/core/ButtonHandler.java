package net.vplaygames.vpm.core;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.vplaygames.vpm.player.PlayerManager;

import java.util.LinkedList;

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
                .editMessageEmbeds(Util.createEmbed(guild, page).build())
                .setActionRows(Util.createButtons(page))
                .queue();
        }
    }
}
