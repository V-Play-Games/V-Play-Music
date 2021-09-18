package net.vplaygames.TheChaosTrilogy.commands.trilogy;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.vplaygames.TheChaosTrilogy.commands.AbstractBotCommand;
import net.vplaygames.TheChaosTrilogy.core.Bot;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;

public class GameCommand extends AbstractBotCommand {
    public GameCommand() {
        super("game", "Continue playing from where you left off");
    }

    @Override
    public boolean runChecks(CommandReceivedEvent e) {
        if (Bot.players.containsKey(e.getAuthor().getIdLong())) {
            e.send("Please start your journey with `v!start` command first").queue();
            return false;
        }
        return true;
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
        Bot.dialogueMap.get(Bot.players.get(e.getAuthor().getIdLong()).getPosition()).send(e);
    }
}
