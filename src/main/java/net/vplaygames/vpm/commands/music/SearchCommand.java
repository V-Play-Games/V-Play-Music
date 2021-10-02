package net.vplaygames.vpm.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vplaygames.vpm.commands.AbstractBotCommand;
import net.vplaygames.vpm.core.Bot;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.SharedImplementation;
import net.vplaygames.vpm.core.Util;
import net.vplaygames.vpm.player.PlayerManager;

public class SearchCommand extends AbstractBotCommand {
    public SearchCommand() {
        super("search", "Searches a track");
        setMinArgs(1);
        addOption(OptionType.STRING, "track", "Type the name of the song you want to search", true);
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
        String id = e.getGuild().getId() + "-" + System.currentTimeMillis();
        PlayerManager.getInstance().loadItem("ytsearch:" + track, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                e.send("Only one result found\n"+Util.toString(track)).queue();
                PlayerManager.getPlayer(e.getGuild()).trackLoaded(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                Bot.searchResults.put(id, playlist.getTracks());
                e.send(SharedImplementation.Search.createEmbed(playlist.getTracks(), 0).build(), "Search Results")
                    .addActionRows(SharedImplementation.Search.createRows(playlist.getTracks(), e.getAuthor().getId(), id, 0))
                    .queue();
            }

            @Override
            public void noMatches() {
                e.send("No Results Found").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                e.send(exception.severity == FriendlyException.Severity.COMMON
                    ? exception.getMessage()
                    : "Something broke while searching the track!").queue();
            }
        });
    }
}
