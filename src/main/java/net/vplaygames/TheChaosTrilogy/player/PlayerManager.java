package net.vplaygames.TheChaosTrilogy.player;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Guild;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager extends DefaultAudioPlayerManager {
    private static PlayerManager instance;

    private Map<Long, GuildAudioManager> managers;

    private PlayerManager() {
        managers = new HashMap<>();

        AudioSourceManagers.registerRemoteSources(this);
        AudioSourceManagers.registerLocalSource(this);
    }

    public static PlayerManager getInstance() {
        return instance == null ? instance = new PlayerManager() : instance;
    }

    public GuildAudioManager getMusicManager(Guild guild) {
        return managers.computeIfAbsent(guild.getIdLong(), guildId -> new GuildAudioManager(this, guild.getAudioManager()));
    }

    public void loadAndPlay(CommandReceivedEvent e, String trackUrl) {
        TrackLoadResultHandler.load(e, trackUrl, getMusicManager(e.getGuild()));
    }
}
