package net.vplaygames.vpm.player;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Guild;
import net.vplaygames.vpm.core.CommandReceivedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PlayerManager extends DefaultAudioPlayerManager {
    private static PlayerManager instance;

    private Map<Long, MusicPlayer> players;

    private PlayerManager() {
        players = new HashMap<>();

        AudioSourceManagers.registerRemoteSources(this);
        AudioSourceManagers.registerLocalSource(this);
    }

    public static PlayerManager getInstance() {
        return instance == null ? instance = new PlayerManager() : instance;
    }

    public void forEach(Consumer<MusicPlayer> consumer) {
        players.values().forEach(consumer);
    }

    public MusicPlayer getPlayer(Guild guild) {
        return getPlayer(guild.getIdLong());
    }

    public MusicPlayer getPlayer(long guildId) {
        return players.computeIfAbsent(guildId, id -> ((MusicPlayer) createPlayer()).configure(id));
    }

    @Override
    protected MusicPlayer constructPlayer() {
        return new MusicPlayer();
    }

    public void loadAndPlay(CommandReceivedEvent e, String trackUrl, boolean isSearched) {
        getPlayer(e.getGuild()).loadAndPlay(trackUrl, e, isSearched);
    }
}
