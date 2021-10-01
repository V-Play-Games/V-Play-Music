package net.vplaygames.vpm.commands.music;

import net.vplaygames.vpm.commands.SharedImplementationCommand;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.SharedImplementation;

public class QueueCommand extends SharedImplementationCommand {
    public QueueCommand() {
        super("queue", "View the queue", "q");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        e.send(SharedImplementation.Queue.createEmbed(e.getGuild(), 0).build(), "queue")
            .addActionRows(SharedImplementation.Queue.createButtons(0))
            .queue();
    }
}
