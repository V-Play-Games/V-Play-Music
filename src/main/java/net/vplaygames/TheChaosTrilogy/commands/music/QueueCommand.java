package net.vplaygames.TheChaosTrilogy.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.vplaygames.TheChaosTrilogy.commands.SharedImplementationCommand;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Util;
import net.vplaygames.TheChaosTrilogy.player.PlayerManager;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class QueueCommand extends SharedImplementationCommand {
    //TODO: Command Description
    public QueueCommand() {
        super("queue", "View the queue");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        Queue<AudioTrack> queue = PlayerManager.getInstance().getMusicManager(e.getGuild()).getQueue();
        if (e.getArgs().size() > 1 && e.getArg(1).equals("clear")) {
            queue.clear();
            e.send("Boom! Queue empty.").queue();
            return;
        }
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
