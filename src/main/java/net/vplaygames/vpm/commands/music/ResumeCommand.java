package net.vplaygames.vpm.commands.music;

import net.vplaygames.vpm.commands.SharedImplementationCommand;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.Util;
import net.vplaygames.vpm.player.MusicPlayer;
import net.vplaygames.vpm.player.PlayerManager;

public class ResumeCommand extends SharedImplementationCommand {
    public ResumeCommand() {
        super("resume", "Stops the current song");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        if (!Util.canJoinVC(e)) {
            return;
        }
        MusicPlayer player = PlayerManager.getInstance().getPlayer(e.getGuild());
        if (player.getPlayingTrack() == null) {
            e.send("Resume? Resume what? Nothin' playin' in 'ere.").queue();
            return;
        }
        if (!player.isPaused()) {
            e.send("Resume? Resume what? Isn't it playing already?").queue();
            return;
        }
        player.setPaused(false);
        e.send("Resumed the track.").queue();
    }
}
