package net.vplaygames.TheChaosTrilogy.commands.music;

import net.vplaygames.TheChaosTrilogy.commands.SharedImplementationCommand;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Util;
import net.vplaygames.TheChaosTrilogy.player.GuildAudioManager;
import net.vplaygames.TheChaosTrilogy.player.PlayerManager;

public class LoopQueueCommand extends SharedImplementationCommand {
    public LoopQueueCommand() {
        super("loopqueue", "Shows info on currently playing track if any", "lq");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        if (!Util.canJoinVC(e)) {
            return;
        }
        GuildAudioManager manager = PlayerManager.getInstance().getMusicManager(e.getGuild());
        manager.toggleLoopQueue();
        e.send("Set queue loop to " + manager.isLoopQueue()).queue();
    }
}
