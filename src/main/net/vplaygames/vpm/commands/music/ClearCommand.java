package net.vplaygames.vpm.commands.music;

import net.vplaygames.vpm.commands.SharedImplementationCommand;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.Util;
import net.vplaygames.vpm.player.PlayerManager;

public class ClearCommand extends SharedImplementationCommand {
    public ClearCommand() {
        super("clear", "Clear the queue");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        if (!Util.canJoinVC(e)) {
            return;
        }
        PlayerManager.getPlayer(e.getGuild()).getQueue().clear();
        e.send("Boom! Queue empty.").queue();
    }
}
