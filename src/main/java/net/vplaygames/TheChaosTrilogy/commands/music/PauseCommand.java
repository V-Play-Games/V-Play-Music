package net.vplaygames.TheChaosTrilogy.commands.music;

import net.vplaygames.TheChaosTrilogy.commands.SharedImplementationCommand;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Util;
import net.vplaygames.TheChaosTrilogy.player.GuildAudioManager;
import net.vplaygames.TheChaosTrilogy.player.PlayerManager;

public class PauseCommand extends SharedImplementationCommand {
    public PauseCommand() {
        super("pause", "Stops the current song");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        if (!Util.canJoinVC(e)) {
            return;
        }
        GuildAudioManager manager = PlayerManager.getInstance().getMusicManager(e.getGuild());
        if (manager.getPlayingTrack() == null) {
            e.send("Pause? Pause what? Nothin' playin' in 'ere.").queue();
            return;
        }
        if (manager.isPaused()) {
            e.send("Pause? Pause what? Isn't it paused already?").queue();
            return;
        }
        manager.setPaused(true);
        e.send("Paused the track.").queue();
    }
}
