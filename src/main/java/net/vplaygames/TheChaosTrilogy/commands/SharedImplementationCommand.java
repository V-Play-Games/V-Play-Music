package net.vplaygames.TheChaosTrilogy.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;

public abstract class SharedImplementationCommand extends AbstractBotCommand {
    public SharedImplementationCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) throws Exception {
        execute(e);
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) throws Exception {
        execute(e);
    }

    public abstract void execute(CommandReceivedEvent e) throws Exception;
}
