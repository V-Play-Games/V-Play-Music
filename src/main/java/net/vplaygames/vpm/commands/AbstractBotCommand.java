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
package net.vplaygames.vpm.commands;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.vplaygames.vpm.core.Bot;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.Ratelimit;
import net.vplaygames.vpm.core.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static net.vplaygames.vpm.core.Bot.INVALID_INPUTS;

public abstract class AbstractBotCommand extends CommandData implements BotCommand {
    public final Map<Long, Ratelimit> ratelimited;
    private int minArgs;
    private int maxArgs;
    private long cooldown;

    public AbstractBotCommand(String name, String description, String... aliases) {
        super(name, description);
        this.ratelimited = new HashMap<>();
        Bot.commands.put(name, this);
        for (String alias : aliases) {
            Bot.commands.put(alias, this);
        }
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

    public void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }

    public void run(CommandReceivedEvent e) {
        long aid = e.getAuthor().getIdLong();
        Ratelimit rl = ratelimited.get(aid);
        if (rl != null && calculateCooldownLeft(rl.inflictedAt) >= 0) {
            onRatelimit(e);
            return;
        }
        if (!runChecks(e)) {
            return;
        }
        int args = e.getArgs() != null ? e.getArgs().size() - 1 : 0;
        if (!e.isSlashCommand && (minArgs > args || args > (maxArgs == 0 ? args : maxArgs))) {
            onInsufficientArgs(e);
            return;
        }
        try {
            if (e.isSlashCommand) {
                onSlashCommandRun(e.slash, e);
            } else {
                onCommandRun(e);
            }
            ratelimit(aid);
        } catch (Exception exc) {
            e.send("There was some trouble processing your request. Please contact VPG.").queue(
                o -> {
                },
                x -> e.getChannel()
                    .sendMessage("There was some trouble processing your request. Please contact VPG.")
                    .queue()
            );
            e.reportTrouble(exc);
        } finally {
            e.log();
        }
    }

    public void onButtonClick(ButtonClickEvent e, String input) {
    }

    public abstract void onCommandRun(CommandReceivedEvent e) throws Exception;

    public abstract void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) throws Exception;

    public void finalizeCommand(Command c) {
    }

    public void onRatelimit(CommandReceivedEvent e) {
        Ratelimit rl = ratelimited.get(e.getAuthor().getIdLong());
        if (!rl.informed) {
            e.forceNotLog();
            e.send("You have to wait for **")
                .append(Util.msToString(calculateCooldownLeft(rl.inflictedAt)))
                .append("** before using this command again.")
                .setEphemeral(true)
                .queue();
            rl.informed = true;
        }
    }

    public void onInsufficientArgs(CommandReceivedEvent e) {
        e.send(INVALID_INPUTS).queue();
    }

    private long calculateCooldownLeft(long inflictedAt) {
        return cooldown + inflictedAt - System.currentTimeMillis();
    }

    public void onHelpNeeded(CommandReceivedEvent e) {
    }

    public boolean runChecks(CommandReceivedEvent e) {
        return true;
    }

    @Override
    public String toString() {
        return Bot.PREFIX + name;
    }

    public Map<Long, Ratelimit> getRateLimited() {
        return ratelimited;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public void setCooldown(long cooldown, TimeUnit cooldownUnit) {
        this.cooldown = (cooldownUnit == null ? TimeUnit.MILLISECONDS : cooldownUnit).toMillis(cooldown);
    }

    public void ratelimit(long userId) {
        ratelimited.put(userId, new Ratelimit(userId));
    }
}
