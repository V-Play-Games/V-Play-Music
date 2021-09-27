package net.vplaygames.vpm.commands.music;

import net.dv8tion.jda.api.entities.Member;
import net.vplaygames.vpm.commands.SharedImplementationCommand;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.Util;
import net.vplaygames.vpm.player.MusicPlayer;
import net.vplaygames.vpm.player.PlayerManager;

import java.util.List;

public class SkipCommand extends SharedImplementationCommand {
    public SkipCommand() {
        super("skip", "Skips the current playing track if any", "s");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        if (!Util.canJoinVC(e)) {
            return;
        }
        MusicPlayer player = PlayerManager.getPlayer(e.getGuild());
        if (player.getPlayingTrack() == null) {
            e.send("There's nothin' playin' in 'ere. Party's over. Let's have an after-party whaddaya think?").queue();
            return;
        }
        List<Member> listeningMembers = Util.getListeningMembers(player.getConnectedVC());
        if (listeningMembers.size() == 1 && listeningMembers.get(0).equals(e.getMember())) {
            player.playNext();
            e.send("Successfully skipped!").queue();
            return;
        }
        player.skip(e, listeningMembers);
    }
}
