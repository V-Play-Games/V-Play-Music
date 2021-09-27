package net.vplaygames.vpm.commands.music;

import net.vplaygames.vpm.commands.SharedImplementationCommand;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.Util;
import net.vplaygames.vpm.player.MusicPlayer;
import net.vplaygames.vpm.player.PlayerManager;

public class LoopQueueCommand extends SharedImplementationCommand {
    public LoopQueueCommand() {
        super("loopqueue", "Shows info on currently playing track if any", "lq");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        if (!Util.canJoinVC(e)) {
            return;
        }
        MusicPlayer manager = PlayerManager.getInstance().getPlayer(e.getGuild());
        manager.toggleLoopQueue();
        e.send("Set queue loop to " + manager.isLoopQueue()).queue();
    }
}
