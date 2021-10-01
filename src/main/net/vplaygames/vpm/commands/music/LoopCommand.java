package net.vplaygames.vpm.commands.music;

import net.vplaygames.vpm.commands.SharedImplementationCommand;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.Util;
import net.vplaygames.vpm.player.MusicPlayer;
import net.vplaygames.vpm.player.PlayerManager;

public class LoopCommand extends SharedImplementationCommand {
    public LoopCommand() {
        super("loop", "Shows info on currently playing track if any");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        if (!Util.canJoinVC(e)) {
            return;
        }
        MusicPlayer player = PlayerManager.getPlayer(e.getGuild());
        player.toggleLoop();
        e.send("Set loop to " + player.isLoop()).queue();
    }
}
