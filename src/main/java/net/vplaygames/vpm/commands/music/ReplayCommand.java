package net.vplaygames.vpm.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.vplaygames.vpm.commands.SharedImplementationCommand;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.player.PlayerManager;

public class ReplayCommand extends SharedImplementationCommand {
    public ReplayCommand() {
        super("replay", "Remove the given track from the queue");
    }

    public void execute(CommandReceivedEvent e) {
        AudioTrack track = PlayerManager.getInstance().getMusicManager(e.getGuild()).getPlayingTrack();
        if (track == null) {
            e.send("Nothin' Playin' in 'ere").queue();
            return;
        }
        track.setPosition(0);
        e.send("Replayed from start").queue();
    }
}
