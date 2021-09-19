package net.vplaygames.TheChaosTrilogy.commands.music;

import net.vplaygames.TheChaosTrilogy.commands.SharedImplementationCommand;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Util;
import net.vplaygames.TheChaosTrilogy.player.GuildAudioManager;
import net.vplaygames.TheChaosTrilogy.player.PlayerManager;

public class ResumeCommand extends SharedImplementationCommand {
    public ResumeCommand() {
        super("resume", "Stops the current song");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        if (!Util.canJoinVC(e)) {
            return;
        }
        GuildAudioManager manager = PlayerManager.getInstance().getMusicManager(e.getGuild());
        if (manager.getPlayingTrack() == null) {
            e.send("Resume? Resume what? Nothin' playin' in 'ere.").queue();
            return;
        }
        if (!manager.isPaused()) {
            e.send("Resume? Resume what? Isn't it playing already?").queue();
            return;
        }
        manager.setPaused(false);
        e.send("Resumed the track.").queue();
    }
}
