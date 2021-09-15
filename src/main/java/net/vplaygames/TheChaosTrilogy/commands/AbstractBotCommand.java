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
package net.vplaygames.TheChaosTrilogy.commands;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.vplaygames.TheChaosTrilogy.core.Bot;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Ratelimit;
import net.vplaygames.TheChaosTrilogy.core.Util;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static net.vplaygames.TheChaosTrilogy.core.Bot.INVALID_INPUTS;

public abstract class AbstractBotCommand extends CommandData implements BotCommand {
    private int minArgs;
    private int maxArgs;
    private long cooldown;
    protected final String name;
    public final HashMap<Long, Ratelimit> ratelimited;

    public AbstractBotCommand(String name, String description, String... aliases) {
        super(name, description);
        this.name = name;
        this.ratelimited = new HashMap<>();
        Bot.commands.put(name, this);
        for (String alias : aliases) {
            Bot.commands.put(alias, this);
        }
    }

    public void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }

    public int getMaxArgs() {
        return maxArgs;
    }

    public void setMaxArgs(int maxArgs) {
        this.maxArgs = maxArgs;
    }

    public int getMinArgs() {
        return minArgs;
    }

    public void run(CommandReceivedEvent e) {
        long aid = e.getAuthor().getIdLong();
        Ratelimit rl = ratelimited.get(aid);
        if (rl != null && System.currentTimeMillis() <= cooldown + rl.inflictedAt) {
            onRatelimit(e);
            return;
        }
        if (runChecks(e)) {
            return;
        }
        int args = e.getArgs() != null ? e.getArgs().size() - 1 : 0;
        if (!e.isSlashCommand && (minArgs > args || args > (maxArgs == 0 ? args : maxArgs))) {
            onInsufficientArgs(e);
            return;
        }
        e.getChannel().sendTyping().queue(x -> {
            try {
                if (e.isSlashCommand) {
                    onSlashCommandRun(e.slash, e);
                } else {
                    onCommandRun(e);
                }
                ratelimited.put(aid, new Ratelimit(aid));
            } catch (Exception exc) {
                e.send("There was some trouble processing your request. Please try Again Later.").queue();
                e.reportTrouble(exc);
            } finally {
                e.log();
            }
        });
    }

    public void onButtonClick(ButtonClickEvent e, String input) {}

    public abstract void onCommandRun(CommandReceivedEvent e) throws Exception;

    public abstract void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) throws Exception;

    public void finalizeCommand(Command c) {}

    public void onRatelimit(CommandReceivedEvent e) {
        Ratelimit rl = ratelimited.get(e.getAuthor().getIdLong());
        if (!rl.informed) {
            e.forceNotLog();
            String waitMessage = "You have to wait for **" + Util.msToString(calculateCooldownLeft(rl.inflictedAt)) + "** before using this command again.";
            if (e.isSlashCommand)
                e.slash.reply(waitMessage).setEphemeral(true).queue();
            else
                e.getChannel().sendMessage(waitMessage).queue();
            rl.informed = true;
        }
    }

    public void onInsufficientArgs(CommandReceivedEvent e) {
        e.send(INVALID_INPUTS).queue();
    }

    private long calculateCooldownLeft(long inflictedAt) {
        return cooldown + inflictedAt - System.currentTimeMillis();
    }

    public void onHelpNeeded(CommandReceivedEvent e) {}

    public boolean runChecks(CommandReceivedEvent e) {
        return false;
    }

    @Override
    public String toString() {
        return Bot.PREFIX + name;
    }

    public HashMap<Long, Ratelimit> getRateLimited() {
        return ratelimited;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public void setCooldown(long cooldown, TimeUnit cooldownUnit) {
        this.cooldown = (cooldownUnit == null ? TimeUnit.MILLISECONDS : cooldownUnit).toMillis(cooldown);
    }
}