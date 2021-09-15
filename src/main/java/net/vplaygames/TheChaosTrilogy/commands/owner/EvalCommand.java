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
package net.vplaygames.TheChaosTrilogy.commands.owner;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.commands.OwnerCommand;

import javax.script.*;
import java.util.Arrays;
import java.util.List;

public class EvalCommand extends OwnerCommand {
    public static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");

    public EvalCommand() {
        super("eval", "Evaluates an expression, or a piece of code");
        addOption(OptionType.STRING, "script", "The expression or code to be evaluated", true);
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) throws Exception {
        execute(e, slash.getOption("script").getAsString());
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) throws Exception {
        List<String> lines = Arrays.asList(e.content.split("\n"));
        String script = String.join("\n", lines.subList(2, lines.size() - 1));
        execute(e, script);
    }

    public void execute(CommandReceivedEvent e, String script) throws ScriptException {
        long startTime = System.currentTimeMillis();
        Bindings bindings = new SimpleBindings();
        bindings.put("e", e);
        bindings.put("event", e);
        bindings.put("guild", e.getGuild());
        bindings.put("channel", e.getChannel());
        bindings.put("author", e.getAuthor());
        bindings.put("jda", e.getJDA());
        bindings.put("script", script);
        bindings.put("startTime", startTime);
        Object result = engine.eval(script, bindings);
        e.send("Successfully Executed in " + (System.currentTimeMillis() - startTime) + " ms\nResult:`" + (result == null ? "" : result.toString()) + "`").queue();
    }
}
