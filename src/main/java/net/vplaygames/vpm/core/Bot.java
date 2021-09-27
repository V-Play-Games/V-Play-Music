/*
 * Copyright 2020-2021 Vaibhav Nargwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.vplaygames.vpm.core;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.vplaygames.vpm.commands.AbstractBotCommand;
import net.vplaygames.vpm.commands.BotCommand;
import net.vplaygames.vpm.entities.Entity;
import net.vplaygames.vpm.entities.EntityInitInfo;
import net.vplaygames.vpm.entities.Guess;
import net.vplaygames.vpm.player.PlayerManager;

import javax.security.auth.login.LoginException;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;

public class Bot {
    public static final String VERSION = "0.0.1";
    public static final String TOKEN = System.getenv("TOKEN");
    public static final String PREFIX = System.getenv("PREFIX");
    public static final String SUPPORT_SERVER_INVITE = "https://discord.gg/amvPsGU";
    public static final String INVALID_INPUTS = "Invalid Amount of Inputs!";
    public static final long BOT_OWNER = 701660977258561557L;
    public static final long RESOURCE_SERVER = 891356944927961108L;
    public static final long LOG_CATEGORY = 891362232066256906L;
    public static final int MAX_SHARDS = 10;
    public static final AtomicLong lastCommandId = new AtomicLong(1);
    public static final Map<String, Guess> guessMap = new HashMap<>();
    public static final Map<String, ActionHandler> actionHandlers = new HashMap<>();
    public static final Map<String, ButtonHandler> buttonHandlers = new HashMap<>();
    public static final Map<String, EntityInitInfo<?>> initInfoMap = new HashMap<>();
    public static final Map<Long, GuessGame> guessGamePlayers = new HashMap<>();
    public static final Map<String, AbstractBotCommand> commands = new HashMap<>();
    public static final Map<Integer, Long> shardLoggers = new HashMap<>();
    public static ShardManager shardManager;
    public static ScanResult scanResult;
    public static boolean closed = false;
    public static int syncCount;
    public static Instant bootTime;

    public static void start() throws LoginException {
        shardManager = DefaultShardManagerBuilder.createDefault(Bot.TOKEN)
            .enableIntents(DIRECT_MESSAGES, GUILD_MEMBERS, GUILD_MESSAGES, GUILD_VOICE_STATES, GUILD_EMOJIS)
            .addEventListeners(EventHandler.getInstance())
            .setShardsTotal(MAX_SHARDS)
            .build();
    }

    public static void init() {
        syncCount = 0;
        startSync();
        loadData();
        loadLoggers();
        loadCommands();
        setDefaultActivity();
        setBooted();
        getLogChannel(0).sendMessage("Bot is up!").queue();
    }

    public static TextChannel getLogChannel(JDA jda) {
        return getLogChannel(jda.getShardInfo().getShardId());
    }

    public static TextChannel getLogChannel(int shardId) {
        return shardManager.getTextChannelById(shardLoggers.get(shardId));
    }

    public static TextChannel getSyncChannel() {
        return shardManager.getTextChannelById(shardLoggers.get(-1));
    }

    public static ShardManager getShardManager() {
        return shardManager;
    }

    public static JDA getPrimaryShard() {
        return shardManager.getShardById(0);
    }

    public static void loadData() {
        initInfoMap.putAll(getScanResult()
            .getAllClasses()
            .stream()
            .filter(x -> !x.isAbstract() && !x.isInterface() && x.implementsInterface(Entity.class.getName()))
            .map(ClassInfo::loadClass)
            .map(c -> {
                try {
                    return c.getMethod("getInfo");
                } catch (NoSuchMethodException e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(m -> m.getDeclaringClass().getName(), m -> {
                try {
                    return (EntityInitInfo<?>) m.invoke(null);
                } catch (Exception e) {
                    throw new InternalError(e);
                }
            })));
        initInfoMap.values().forEach(Bot::loadEntity);
        getShardManager().getGuilds().forEach(guild -> PlayerManager.getInstance().getPlayer(guild));
    }

    public static <T extends Entity> void loadEntity(EntityInitInfo<T> info) {
        try (InputStream stream = info.fileUrl.openStream()) {
            DataArray.fromJson(stream)
                .stream(DataArray::getObject)
                .filter(data -> !data.keys().isEmpty())
                .map(info.entityConstructor)
                .forEach(entity -> info.entityMap.put(entity.getId(), entity));
            System.out.println("Loaded " + info.fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startSync() {
        getPrimaryShard().getRateLimitPool().scheduleWithFixedDelay(() -> {
            syncCount++;
            System.out.println("Performing Sync [" + syncCount + "]");
            getSyncChannel().sendMessage("Sync [" + syncCount + "]").queue();
        }, 1, 1, TimeUnit.MINUTES);
    }

    public static void loadLoggers() {
        Guild resources = shardManager.getGuildById(RESOURCE_SERVER);
        Category category = resources.getCategoryById(LOG_CATEGORY);
        assert resources != null;
        shardManager.getShardCache()
            .stream()
            .map(JDA::getShardInfo)
            .mapToInt(JDA.ShardInfo::getShardId)
            .forEach(id -> {
                List<TextChannel> channels = resources.getTextChannelsByName("shard-" + id, true);
                if (channels.isEmpty()) {
                    category.createTextChannel("shard-" + id).queue(tc -> addLogger(id, tc.getIdLong()));
                } else {
                    addLogger(id, channels.get(0).getIdLong());
                }
            });
        List<TextChannel> channels = resources.getTextChannelsByName("sync", true);
        if (channels.isEmpty()) {
            category.createTextChannel("sync").queue(tc -> addLogger(-1, tc.getIdLong()));
        } else {
            addLogger(-1, channels.get(0).getIdLong());
        }
    }

    public static void addLogger(int index, long id) {
        shardLoggers.put(index, id);
        if (0 <= index && index < MAX_SHARDS) {
            getLogChannel(index)
                .sendMessage("GuildCache:\n")
                .append(shardManager.getShardById(index)
                    .getGuildCache()
                    .stream()
                    .map(Guild::toString)
                    .collect(Collectors.joining("\n")))
                .queue();
        }
    }

    public static void loadCommands() {
        loadAllInstancesOf(BotCommand.class, o -> System.out.println("Loaded " + o + " Command"));
        loadAllInstancesOf(ActionHandler.class, handler -> {
            actionHandlers.put(handler.getName(), handler);
            System.out.println("Loaded " + handler.getName() + " action handler");
        });
        loadAllInstancesOf(ButtonHandler.class, handler -> {
            buttonHandlers.put(handler.getName(), handler);
            System.out.println("Loaded " + handler.getName() + " button handler");
        });
        Set<AbstractBotCommand> commandSet = new HashSet<>(commands.values());
        shardManager.getShards().forEach(shard -> shard.updateCommands()
            .addCommands(commandSet)
            .queue(c -> c.forEach(command -> commands.get(command.getName()).finalizeCommand(command))));
    }

    @SuppressWarnings("unchecked")
    public static <T> void loadAllInstancesOf(Class<T> _interface, Consumer<T> newInstanceProcessor) {
        Map<Class<?>, Exception> errors = new HashMap<>();
        getScanResult()
            .getAllClasses()
            .stream()
            .filter(x -> !x.isAbstract() && !x.isInterface() && x.implementsInterface(_interface.getName()))
            .forEach(x -> {
                try {
                    T newObject = (T) x.loadClass().getConstructor().newInstance();
                    newInstanceProcessor.accept(newObject);
                } catch (Exception e) {
                    errors.put(x.loadClass(), e);
                }
            });
        errors.forEach((k, v) -> {
            System.out.println("Failed to loadAndPlay " + k.getSimpleName() + "\n");
            v.printStackTrace();
        });
    }

    public static ScanResult getScanResult() {
        return scanResult == null ? scanResult = new ClassGraph().enableClassInfo().scan() : scanResult;
    }

    public static void setDefaultActivity() {
        SnowflakeCacheView<Guild> cache = shardManager.getGuildCache();
        shardManager.setActivity(Activity.playing("with " + cache.stream()
            .map(Guild::getMemberCache)
            .flatMap(CacheView::stream)
            .mapToLong(Member::getIdLong)
            .distinct()
            .count() + " people in " + cache.size() + " servers"));
    }

    public static void setBooted() {
        bootTime = Instant.now();
    }
}
