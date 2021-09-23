package net.vplaygames.vpm.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vplaygames.vpm.commands.AbstractBotCommand;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.Util;
import net.vplaygames.vpm.player.PlayerManager;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class RemoveCommand extends AbstractBotCommand {
    public RemoveCommand() {
        super("remove", "Remove the given track from the queue");
        addOption(OptionType.INTEGER, "index", "Index of the track to be removed", true);
        setMinArgs(1);
        setMaxArgs(1);
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e, Util.toInt(e.getArg(1)));
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) {
        execute(e, (int) slash.getOption("index").getAsLong());
    }

    public void execute(CommandReceivedEvent e, int index) {
        LinkedList<AudioTrack> queue = PlayerManager.getInstance().getMusicManager(e.getGuild()).getQueue();
        if (index < 1 || index > queue.size()) {
            e.send("Invalid index.").queue();
            return;
        }
        AtomicInteger current = new AtomicInteger(0);
        AudioTrack track = queue.remove(index);
        if (track == null) {
            e.send("Something went wrong, Please try again later").queue();
            return;
        }
        queue.remove(track);
        e.send("Removed " + Util.toString(track)).queue();
    }
}
