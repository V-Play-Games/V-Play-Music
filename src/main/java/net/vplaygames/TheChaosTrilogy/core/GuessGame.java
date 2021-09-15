package net.vplaygames.TheChaosTrilogy.core;

import net.vplaygames.TheChaosTrilogy.entities.Guess;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GuessGame {
    public static Random random = new Random();
    public static List<String> guessIds = new ArrayList<>(Bot.guessMap.keySet());
    private long userId;
    private Guess guess_;
    private ScheduledFuture<?> gameEndingAction;

    public GuessGame(CommandReceivedEvent e) {
        this.userId = e.getAuthor().getIdLong();
        this.guess_ = Bot.guessMap.get(guessIds.get(random.nextInt(guessIds.size())));
        gameEndingAction = e.getChannel()
            .sendMessage("Aw man, you took too long to reply.\nThe answer was " + guess_.getName())
            .queueAfter(30, TimeUnit.SECONDS, x -> Bot.guessGamePlayers.remove(userId));
        Bot.guessGamePlayers.put(userId, this);
    }

    public boolean guessed(String guess) {
        if (guess_.getName().equalsIgnoreCase(guess)) {
            gameEndingAction.cancel(true);
            Bot.guessGamePlayers.remove(userId);
            return true;
        }
        return false;
    }

    public long getUserId() {
        return userId;
    }

    public Guess getGuess() {
        return guess_;
    }
}
