package net.vplaygames.vpm.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vplaygames.vpm.commands.AbstractBotCommand;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.Util;
import net.vplaygames.vpm.player.PlayerManager;

public class SeekCommand extends AbstractBotCommand {
    public SeekCommand() {
        super("seek", "Remove the given track from the queue");
        addOption(OptionType.INTEGER, "position", "Index of the track to be removed", true);
        setMinArgs(1);
        setMaxArgs(1);
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e, Util.toInt(e.getArg(1)));
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) {
        execute(e, slash.getOption("position").getAsLong());
    }

    public void execute(CommandReceivedEvent e, long position) {
        AudioTrack track = PlayerManager.getInstance().getMusicManager(e.getGuild()).getPlayingTrack();
        if (track == null) {
            e.send("Nothin' Playin' in 'ere").queue();
            return;
        }
        position *= 1000;
        if (position < 0 || position > track.getDuration()) {
            e.send("Position out of bounds! Must be between 0 and the length of the track (" + Util.toString(track.getDuration()) + ")").queue();
            return;
        }
        track.setPosition(position);
        e.send("Seeked to " + Util.toString(position)).queue();
    }
}
