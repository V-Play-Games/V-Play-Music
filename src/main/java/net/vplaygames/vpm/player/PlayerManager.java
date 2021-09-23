package net.vplaygames.vpm.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Guild;
import net.vplaygames.vpm.core.CommandReceivedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PlayerManager extends DefaultAudioPlayerManager {
    private static PlayerManager instance;

    private Map<Long, GuildMusicManager> managers;

    private PlayerManager() {
        managers = new HashMap<>();

        AudioSourceManagers.registerRemoteSources(this);
        AudioSourceManagers.registerLocalSource(this);
    }

    public static PlayerManager getInstance() {
        return instance == null ? instance = new PlayerManager() : instance;
    }

    public void forEach(Consumer<GuildMusicManager> consumer) {
        managers.values().forEach(consumer);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return getMusicManager(guild.getIdLong());
    }

    public GuildMusicManager getMusicManager(long guildId) {
        return managers.computeIfAbsent(guildId, id -> ((GuildMusicManager) createPlayer()).configure(id));
    }

    @Override
    protected AudioPlayer constructPlayer() {
        return new GuildMusicManager();
    }

    public void loadAndPlay(CommandReceivedEvent e, String trackUrl, boolean isSearched) {
        getMusicManager(e.getGuild()).loadAndPlay(trackUrl, e, isSearched);
    }
}
