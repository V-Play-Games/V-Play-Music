package net.vplaygames.vpm.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.vplaygames.vpm.commands.SharedImplementationCommand;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.Util;
import net.vplaygames.vpm.player.PlayerManager;

import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class QueueCommand extends SharedImplementationCommand {
    public QueueCommand() {
        super("queue", "View the queue");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        LinkedList<AudioTrack> queue = PlayerManager.getInstance().getMusicManager(e.getGuild()).getQueue();
        AtomicInteger i = new AtomicInteger(1);
        e.send(Optional.of(queue.stream()
            .limit(10)
            .map(Util::toString)
            .map(s -> i.getAndIncrement() + ". " + s)
            .collect(Collectors.joining("\n")))
            .map(s -> s.isEmpty() ? null : s)
            .orElse("The Queue is empty, oh c'mon play something smh")).queue();
    }
}
