package net.vplaygames.TheChaosTrilogy.commands.music;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.vplaygames.TheChaosTrilogy.commands.AbstractBotCommand;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Util;
import net.vplaygames.TheChaosTrilogy.player.PlayerManager;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class QueueCommand extends AbstractBotCommand {
    //TODO: Command Description
    public QueueCommand() {
        super("queue", "pain");
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e);
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) {
        execute(e);
    }

    public void execute(CommandReceivedEvent e) {
        AtomicInteger i = new AtomicInteger(1);
        e.send(Optional.of(PlayerManager.getInstance()
            .getMusicManager(e.getGuild())
            .getQueue()
            .stream()
            .limit(10)
            .map(Util::toString)
            .map(s -> i.getAndIncrement() + ". " + s)
            .collect(Collectors.joining("\n")))
            .map(s -> s.isEmpty() ? null : s)
            .orElse("The Queue is empty, oh c'mon play something smh")).queue();
    }
}
