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
package net.vplaygames.TheChaosTrilogy.commands.fun.meme;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vplaygames.TheChaosTrilogy.commands.AbstractBotCommand;
import net.vplaygames.TheChaosTrilogy.core.Bot;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MemeCommand extends AbstractBotCommand {
    public static final List<Meme> randomMemes = new ArrayList<>();
    public final Connection conn = new Connection();

    public MemeCommand() {
        super("meme", "Pulls a random meme from Reddit");
        addOption(OptionType.STRING, "subreddit", "The subreddit to pull a meme from");
        setCooldown(10, TimeUnit.SECONDS);
        setMaxArgs(1);
        Bot.timer.execute(() -> {
            try {
                randomMemes.addAll(conn.getMemes(10));
                randomMemes.addAll(conn.getMemes(10, "PokemonMasters"));
                randomMemes.addAll(conn.getMemes(10, "Pokemon"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public boolean runChecks(CommandReceivedEvent e) {
        if (e.isFromGuild()) {
            e.send("Sorry, but this command cannot be used in DMs.").queue();
            return false;
        }
        return true;
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) throws IOException {
        execute(e, e.getArgs().size() == 2 ? e.getArg(1) : "", ((TextChannel) e.getChannel()).isNSFW());
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) throws Exception {
        execute(e, Util.getString(slash, "subreddit"), ((TextChannel) e.getChannel()).isNSFW());
    }

    public void execute(CommandReceivedEvent e, String subreddit, boolean allowNSFW) throws IOException {
        Meme meme = conn.getMeme(subreddit);
        if (meme.nsfw) {
            if (!allowNSFW) meme = randomMemes.get(new Random().nextInt(randomMemes.size()));
        } else {
            randomMemes.add(meme);
        }
        e.send(new EmbedBuilder()
            .setTitle(meme.title, meme.postLink)
            .setDescription("Meme by u/" + meme.author + " in r/" + meme.subreddit)
            .setImage(meme.url)
            .setFooter(meme.ups + " Upvotes").build(), meme.url).queue();
    }
}
