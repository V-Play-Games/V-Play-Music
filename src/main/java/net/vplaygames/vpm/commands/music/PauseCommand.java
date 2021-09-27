package net.vplaygames.vpm.commands.music;

import net.vplaygames.vpm.commands.SharedImplementationCommand;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.Util;
import net.vplaygames.vpm.player.MusicPlayer;
import net.vplaygames.vpm.player.PlayerManager;

public class PauseCommand extends SharedImplementationCommand {
    public PauseCommand() {
        super("pause", "Stops the current song");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        if (!Util.canJoinVC(e)) {
            return;
        }
        MusicPlayer player = PlayerManager.getInstance().getPlayer(e.getGuild());
        if (player.getPlayingTrack() == null) {
            e.send("Pause? Pause what? Nothin' playin' in 'ere.").queue();
            return;
        }
        if (player.isPaused()) {
            e.send("Pause? Pause what? Isn't it paused already?").queue();
            return;
        }
        player.setPaused(true);
        e.send("Paused the track.").queue();
    }
}
