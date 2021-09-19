package net.vplaygames.TheChaosTrilogy.commands.music;

import net.vplaygames.TheChaosTrilogy.commands.SharedImplementationCommand;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Util;
import net.vplaygames.TheChaosTrilogy.player.GuildAudioManager;
import net.vplaygames.TheChaosTrilogy.player.PlayerManager;

public class SkipCommand extends SharedImplementationCommand {
    public SkipCommand() {
        super("skip", "Skips the current playing track if any");
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
