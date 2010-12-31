/*
 * Copyright 2021 Vaibhav Nargwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.vpg.bot.player;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Guild;
import net.vpg.bot.commands.CommandReceivedEvent;
import net.vpg.bot.framework.Bot;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PlayerManager extends DefaultAudioPlayerManager implements Iterable<MusicPlayer> {
    private static final Map<String, PlayerManager> managers = new HashMap<>();
    final Bot bot;
    private final Map<Long, MusicPlayer> players = new HashMap<>();

    private PlayerManager(Bot bot) {
        this.bot = bot;
        AudioSourceManagers.registerRemoteSources(this);
        AudioSourceManagers.registerLocalSource(this);
        managers.put(bot.getId(), this);
    }

    public static PlayerManager getManager(Bot bot) {
        PlayerManager manager = managers.get(bot.getId());
        return manager == null ? new PlayerManager(bot) : manager;
    }

    public static MusicPlayer getPlayer(CommandReceivedEvent e) {
        return getPlayer(e.getBot(), e.getGuild());
    }

    public static MusicPlayer getPlayer(Bot bot, Guild guild) {
        return getManager(bot).getPlayer(guild.getIdLong());
    }

    public MusicPlayer getPlayer(long guildId) {
        return players.computeIfAbsent(guildId, id -> ((MusicPlayer) createPlayer()).configure(id));
    }

    @Override
    protected MusicPlayer constructPlayer() {
        return new MusicPlayer(this);
    }

    public void loadAndPlay(CommandReceivedEvent e, String trackUrl, boolean isSearched) {
        getPlayer(e).loadAndPlay(trackUrl, e, isSearched);
    }

    @NotNull
    @Override
    public Iterator<MusicPlayer> iterator() {
        return players.values().iterator();
    }
}
