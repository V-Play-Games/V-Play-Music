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
package net.vplaygames.TheChaosTrilogy.core;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.vplaygames.TheChaosTrilogy.commands.AbstractBotCommand;
import net.vplaygames.TheChaosTrilogy.commands.BotCommand;
import net.vplaygames.TheChaosTrilogy.entities.*;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Bot {
    public static final String VERSION = "0.0.1";
    public static final File logFile = new File("logFile.txt");
    public static final File errorFile = new File("errorFile.txt");
    public static final String TOKEN = System.getenv("TOKEN");
    public static final String PREFIX = System.getenv("PREFIX");
    public static final String SUPPORT_SERVER_INVITE = "https://discord.gg/amvPsGU";
    public static final String INVALID_INPUTS = "Invalid Amount of Inputs!";
    public static final long BOT_OWNER = 701660977258561557L;
    public static final long logChannelId = 762950187492179995L;
    public static final long syncChannelId = 762950187492179995L;
    public static final AtomicLong lastCommandId = new AtomicLong(1);
    public static final Map<String, Dialogue> dialogueMap = new HashMap<>();
    public static final Map<String, Guess> guessMap = new HashMap<>();
    public static final Map<String, Pokemon> pokemonMap = new HashMap<>();
    public static final Map<String, Move> moveMap = new HashMap<>();
    public static final Map<String, Ability> abilityMap = new HashMap<>();
    public static final Map<String, ActionHandler> actionHandlers = new HashMap<>();
    public static final Map<String, ButtonHandler> buttonHandlers = new HashMap<>();
    public static final Map<String, EntityInitInfo<?>> initInfoMap = new HashMap<>();
    public static final Map<Long, Player> players = new HashMap<>();
    public static final Map<Long, GuessGame> guessGamePlayers = new HashMap<>();
    public static final Map<String, AbstractBotCommand> commands = new HashMap<>();
    public static JDA jda;
    public static ScanResult scanResult;
    public static boolean closed = false;
    public static boolean rebooted = false;
    public static int syncCount;
    public static Instant instantAtBoot;
    public static String lastRefresh = "never";
    public static ScheduledThreadPoolExecutor timer;
    public static Runnable rebootTasks = () -> {
    };

    static {
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
    }

    public static TextChannel getLogChannel() {
        return jda.getTextChannelById(logChannelId);
    }

    public static TextChannel getSyncChannel() {
        return jda.getTextChannelById(syncChannelId);
    }

    public static JDA getJda() {
        return jda;
    }

    public static void start() throws LoginException {
        jda = JDABuilder.createDefault(Bot.TOKEN,
            GatewayIntent.DIRECT_MESSAGES,
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_VOICE_STATES,
            GatewayIntent.GUILD_EMOJIS)
            .addEventListeners(EventHandler.getInstance())
            .build();
    }

    public static void init() {
        syncCount = 0;
        initData();
        startTimer();
        loadCommands(jda);
        setDefaultActivity(jda);
        setBooted();
        rebootTasks.run();
        //logChannel.sendMessage("I am ready for anything!\n\t-Morty, Johto Gym Leader, 2020").queue();
    }

    public static void initData() {
        initInfoMap.values().forEach(Bot::loadEntity);
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

    public static void startTimer() {
        if (timer != null) timer.shutdown();
        (timer = new ScheduledThreadPoolExecutor(2, r -> new Thread(r, "VPG Timer")))
            .scheduleWithFixedDelay(() -> {
                syncCount++;
                System.out.println("Syncing data [" + syncCount + "]");
                getSyncChannel().sendMessage("Sync [" + syncCount + "]").queue();
                getSyncChannel().sendMessage("logFile").addFile(logFile).queue();
                getSyncChannel().sendMessage("errorFile").addFile(errorFile).queue();
            }, 0, 20, TimeUnit.MINUTES);
    }

    public static void loadCommands(JDA jda) {
        loadAllInstancesOf(BotCommand.class, o -> System.out.println("Loaded " + o + " Command"));
        loadAllInstancesOf(ActionHandler.class, handler -> {
            actionHandlers.put(handler.getName(), handler);
            System.out.println("Loaded " + handler.getName() + " action handler");
        });
        loadAllInstancesOf(ButtonHandler.class, handler -> {
            buttonHandlers.put(handler.getName(), handler);
            System.out.println("Loaded " + handler.getName() + " button handler");
        });
        jda.updateCommands()
            .addCommands(new HashSet<>(commands.values()))
            .queue(c -> c.forEach(command -> commands.get(command.getName()).finalizeCommand(command)));
    }

    public static Player getPlayer(long id) {
        return players.get(id);
    }

    public static Dialogue getDialogue(String id) {
        return dialogueMap.get(id);
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
            System.out.println("Failed to load " + k.getSimpleName() + "\n");
            v.printStackTrace();
        });
    }

    public static ScanResult getScanResult() {
        return scanResult == null ? scanResult = new ClassGraph().enableClassInfo().scan() : scanResult;
    }

    public static void setDefaultActivity(JDA jda) {
        jda.getPresence()
            .setActivity(Activity
                .playing("with " + jda.getGuilds()
                    .stream()
                    .mapToLong(Guild::getMemberCount)
                    .sum() + " people in " + jda.getGuilds().size() + " servers"));
    }

    public static void setBooted() {
        instantAtBoot = Instant.now();
    }
}
