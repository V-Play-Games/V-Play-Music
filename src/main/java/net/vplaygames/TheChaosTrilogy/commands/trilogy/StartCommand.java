package net.vplaygames.TheChaosTrilogy.commands.trilogy;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.vplaygames.TheChaosTrilogy.commands.AbstractBotCommand;
import net.vplaygames.TheChaosTrilogy.core.Bot;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Player;

public class StartCommand extends AbstractBotCommand {
    public StartCommand() {
        super("start", "Starts a new story...");
    }

    @Override
    public boolean runChecks(CommandReceivedEvent e) {
        if (Bot.players.containsKey(e.getAuthor().getIdLong())) {
            e.send("You have already started your journey, Go on and play the game using `v!game` command").queue();
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
        new Player(e.getAuthor().getIdLong());
        Bot.getDialogue("paseagon.welcome").send(e);
    }
}
