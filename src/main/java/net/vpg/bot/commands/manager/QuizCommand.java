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
package net.vpg.bot.commands.manager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.core.Bot;
import net.vpg.bot.event.SlashCommandReceivedEvent;
import net.vpg.bot.event.TextCommandReceivedEvent;
import net.vpg.bot.player.PlayerManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class QuizCommand extends BotCommandImpl implements ManagerCommand {
    Map<Long, Integer> scores = new HashMap<>();

    public QuizCommand(Bot bot) {
        super(bot, "quiz", "quiz");
    }

    @Override
    public void onTextCommandRun(TextCommandReceivedEvent e) {
        switch (e.getArg(0)) {
            case "start":
                PlayerManager.getPlayer(e).setQuizHostId(e.getUser().getIdLong());
                e.send("Starting quiz with host " + e.getUser().getAsMention()).queue();
                break;
            case "end":
                PlayerManager.getPlayer(e).setQuizHostId(0);
                e.send("Starting quiz with host " + e.getUser().getAsMention()).queue();
                break;
            case "update":
                long id = e.getMessage().getMentionedUsers().get(0).getIdLong();
                Integer oldScore = scores.getOrDefault(id, 0);
                scores.put(id, oldScore + 1);
                e.send("Updated " + e.getJDA().getUserById(id).getName() + "'s score to " + (oldScore + 1)).queue();
            case "scores":
                AtomicInteger i = new AtomicInteger();
                e.sendEmbeds(new EmbedBuilder()
                    .setDescription(scores.entrySet()
                        .stream()
                        .map(score -> i.incrementAndGet() + ". <@" + score.getKey() + "> - " + score.getValue() + " points")
                        .collect(Collectors.joining("\n")))
                    .build()).queue();
                break;
            case "clear":
                scores.clear();
                e.send("Cleared scores").queue();
                break;
        }
    }

    @Override
    public void onSlashCommandRun(SlashCommandReceivedEvent e) {
        e.send("Nope.").queue();
    }
}