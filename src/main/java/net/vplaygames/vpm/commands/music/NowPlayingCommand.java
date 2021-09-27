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
            e.send("There's nothing playin' in 'ere. Party's over. Let's have an after party whaddaya think?").queue();
            return;
        }
        AudioTrackInfo info = track.getInfo();
        int progress = (int) (track.getPosition() * 12 / info.length);
        e.send(new EmbedBuilder()
            .setTitle(info.title, info.uri)
            .appendDescription(Util.toString(info.length))
            .appendDescription("/")
            .appendDescription(Util.toString(track.getPosition()))
            .appendDescription("\n")
            .appendDescription(Util.repeat('-', progress))
            .appendDescription(Util.repeat('_', 12 - progress))
            .build(), "now playing")
            .queue();
    }
}
