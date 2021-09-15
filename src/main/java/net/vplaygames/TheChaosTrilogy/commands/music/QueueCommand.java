package net.vplaygames.TheChaosTrilogy.commands.music;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.vplaygames.TheChaosTrilogy.commands.AbstractBotCommand;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Util;
import net.vplaygames.TheChaosTrilogy.player.PlayerManager;

import java.util.stream.Collectors;

public class QueueCommand extends AbstractBotCommand {
    //TODO: Command Description
    public QueueCommand() {
        super("queue", "pain");
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) throws Exception {
        execute(e);
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) throws Exception {
        execute(e);
    }

    public void execute(CommandReceivedEvent e) {
        e.send(PlayerManager.getInstance()
            .getMusicManager(e.getGuild())
        .getQueue()
        .stream()
            .limit(10)
        .map(Util::toString)
        .collect(Collectors.joining("\n"))).queue();
    }
}
