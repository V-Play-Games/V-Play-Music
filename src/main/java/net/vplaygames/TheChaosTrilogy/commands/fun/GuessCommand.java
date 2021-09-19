package net.vplaygames.TheChaosTrilogy.commands.fun;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.vplaygames.TheChaosTrilogy.commands.SharedImplementationCommand;
import net.vplaygames.TheChaosTrilogy.core.Bot;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.GuessGame;

import javax.annotation.Nonnull;

public class GuessCommand extends SharedImplementationCommand {
    public GuessCommand() {
        super("guess", "Guess a Pokemon name by the given description of it");
        Bot.jda.addEventListener(new ListenerAdapter() {
            @Override
            public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent e) {
                GuessCommand.this.onGuildMessageReceived(e);
            }
        });
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        GuessGame game = new GuessGame(e);
        e.send("You have started a new guess game!\nGuess the Pokemon based on its given description in 30 seconds or less!\n> " + game.getGuess().getDescription()).queue();
    }

    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent e) {
        GuessGame game = Bot.guessGamePlayers.get(e.getAuthor().getIdLong());
        if (game != null) {
            boolean isCorrect = game.guessed(e.getMessage().getContentRaw());
            if (isCorrect) {
                e.getChannel().sendMessage("GG! You guessed it right! The answer was " + game.getGuess().getName()).queue();
            } else {
                e.getMessage().addReaction("U+274C").queue();
            }
        }
    }
}
