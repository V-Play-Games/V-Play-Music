package net.vplaygames.vpm.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vplaygames.vpm.commands.AbstractBotCommand;
import net.vplaygames.vpm.core.Bot;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.Util;
import net.vplaygames.vpm.player.PlayerManager;

import java.util.LinkedList;

public class MoveCommand extends AbstractBotCommand {
    public MoveCommand() {
        super("move", "Move songs through the queue");
        addOption(OptionType.INTEGER, "from", "Index of the track to be removed", true);
        addOption(OptionType.INTEGER, "to", "Index of the track to be removed", true);
        setMinArgs(1);
        setMaxArgs(1);
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e, Util.toInt(e.getArg(2)) - 1, Util.toInt(e.getArg(3)) - 1);
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) {
        execute(e, (int) slash.getOption("from").getAsLong() - 1, (int) slash.getOption("to").getAsLong() - 1);
    }

    public void execute(CommandReceivedEvent e, int from, int to) {
        if (!Util.canJoinVC(e)) {
            return;
        }
        LinkedList<AudioTrack> queue = PlayerManager.getPlayer(e.getGuild()).getQueue();
        if (e.getArgs().size() != 4) {
            e.send("Please provide a proper amount of arguments")
                .append("\nFormat: `" + Bot.PREFIX + getName() + " move <from_index> <to_index>`")
                .queue();
            return;
        }
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
    }
}
