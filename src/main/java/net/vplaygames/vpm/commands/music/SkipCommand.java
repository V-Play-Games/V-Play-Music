package net.vplaygames.vpm.commands.music;

import net.vplaygames.vpm.commands.SharedImplementationCommand;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.Util;
import net.vplaygames.vpm.player.GuildAudioManager;
import net.vplaygames.vpm.player.PlayerManager;

public class SkipCommand extends SharedImplementationCommand {
    public SkipCommand() {
        super("skip", "Skips the current playing track if any", "s");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        if (!Util.canJoinVC(e)) {
            return;
        }
        GuildAudioManager manager = PlayerManager.getInstance().getMusicManager(e.getGuild());
        if (manager.getPlayingTrack() == null) {
            e.send("There's nothin' playin' in 'ere. Party's over. Let's have an after-party whaddaya think?").queue();
            return;
        }
        if (Util.getListeningMembers(manager.getConnectedVoiceChannel()).size() == 1) {
            manager.playNext();
            e.send("Successfully skipped!").queue();
            return;
        }
        manager.skip(e);
    }
}
