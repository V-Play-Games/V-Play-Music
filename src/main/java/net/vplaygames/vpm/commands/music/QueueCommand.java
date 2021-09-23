package net.vplaygames.vpm.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.vplaygames.vpm.commands.SharedImplementationCommand;
import net.vplaygames.vpm.core.Bot;
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
        if (e.getArgs().size() > 1) {
            switch (e.getArg(1)) {
                case "clear":
                    queue.clear();
                    e.send("Boom! Queue empty.").queue();
                    break;
                case "move":
                    if (e.getArgs().size() != 4) {
                        e.send("Please provide a proper amount of arguments")
                            .append("\nFormat: `" + Bot.PREFIX + getName() + " move <from_index> <to_index>`")
                            .queue();
                        break;
                    }
                    int from = Util.toInt(e.getArg(2)) - 1;
                    int to = Util.toInt(e.getArg(3)) - 1;
                    if (from < 0 || to < 0) {
                        e.send("Index cannot be in negative!").queue();
                    }
                    if (from >= queue.size() || to >= queue.size()) {
                        e.send("Index more than the queue size!").queue();
                    }
                    AudioTrack toMove = queue.get(from);
                    AudioTrack oldTrack = queue.set(to, toMove);
                    queue.set(from, oldTrack);
                    e.send("Switched " + from + " and " + to).queue();
                default:
                    e.send("Wha-").queue();
            }
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
