package net.vplaygames.TheChaosTrilogy.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.vplaygames.TheChaosTrilogy.commands.SharedImplementationCommand;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Util;
import net.vplaygames.TheChaosTrilogy.player.PlayerManager;

public class NowPlayingCommand extends SharedImplementationCommand {
    public NowPlayingCommand() {
        super("nowplaying", "Shows info on currently playing track if any", "np");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        AudioTrack currentTrack = PlayerManager.getInstance().getMusicManager(e.getGuild()).getPlayingTrack();
        if (currentTrack == null) {
            e.send("There's nothing playin' in 'ere. Party's over. Let's have an after party whaddaya think?").queue();
            return;
        }
        e.send("Now Playing: " + Util.toString(currentTrack)).queue();
    }
}
