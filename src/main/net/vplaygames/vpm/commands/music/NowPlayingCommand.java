package net.vplaygames.vpm.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.vplaygames.vpm.commands.SharedImplementationCommand;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.Util;
import net.vplaygames.vpm.player.PlayerManager;

public class NowPlayingCommand extends SharedImplementationCommand {
    public NowPlayingCommand() {
        super("nowplaying", "Shows info on currently playing track if any", "np");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        AudioTrack track = PlayerManager.getPlayer(e.getGuild()).getPlayingTrack();
        if (track == null) {
            e.send("There's nothin' playin' in 'ere. Party's o'er. Let's have an after party whaddaya think?").queue();
            return;
        }
        AudioTrackInfo info = track.getInfo();
        e.send(new EmbedBuilder()
            .setTitle(info.title, info.uri)
            .appendDescription(Util.getProgressBar(track, 12))
            .appendDescription(" ")
            .appendDescription(Util.toString(track.getPosition()))
            .appendDescription("/")
            .appendDescription(Util.toString(track.getDuration()))
            .build(), "now playing")
            .queue();
    }
}
