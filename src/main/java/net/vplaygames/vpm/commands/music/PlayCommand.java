package net.vplaygames.vpm.commands.music;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vplaygames.vpm.commands.AbstractBotCommand;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.Util;
import net.vplaygames.vpm.player.PlayerManager;

import java.net.URI;
import java.net.URISyntaxException;

public class PlayCommand extends AbstractBotCommand {
    public PlayCommand() {
        super("play", "Play a track", "p");
        setMinArgs(1);
        addOption(OptionType.STRING, "track", "Type the name or URL of the song you want to play", true);
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e, String.join(" ", e.getArgsFrom(1)));
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) {
        execute(e, Util.getString(slash, "track"));
    }

    public void execute(CommandReceivedEvent e, String track) {
        if (!Util.canJoinVC(e)) {
            return;
        }
        while (track.startsWith("<") && track.endsWith(">")) {
            track = track.substring(1, track.length() - 1);
        }
        if (isUri(track)) {
            track = "ytsearch:" + track;
        }
        PlayerManager.getInstance().loadAndPlay(e, track, false);
    }

    boolean isUri(String uri) {
        try {
            new URI(uri);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
