package net.vplaygames.vpm.commands.music;

import net.vplaygames.vpm.commands.SharedImplementationCommand;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.Util;

public class QueueCommand extends SharedImplementationCommand {
    public QueueCommand() {
        super("queue", "View the queue", "q");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        e.send(Util.createEmbed(e.getGuild(), 0).build(), "queue")
            .addActionRows(Util.createButtons(0))
            .queue();
    }
}
