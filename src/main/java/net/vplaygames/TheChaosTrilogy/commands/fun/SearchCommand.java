package net.vplaygames.TheChaosTrilogy.commands.fun;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vplaygames.TheChaosTrilogy.commands.AbstractBotCommand;
import net.vplaygames.TheChaosTrilogy.core.Bot;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Util;
import net.vplaygames.TheChaosTrilogy.entities.Move;
import net.vplaygames.TheChaosTrilogy.entities.Pokemon;

import java.util.stream.Collectors;

public class SearchCommand extends AbstractBotCommand {
    public SearchCommand() {
        super("search", "Search for a Pokemon, Move or Ability");
        addOption(OptionType.STRING, "term", "The term to search", true);
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) throws Exception {
        execute(e, String.join(" ", e.getArgsFrom(1)));
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) throws Exception {
        execute(e, Util.getString(slash, "term"));
    }

    public void execute(CommandReceivedEvent e, String searchTerm) {
        String toSearch = String.join("-", searchTerm.split(" ")).toLowerCase();
        Pokemon p = Bot.pokemonMap.get(toSearch);
        if (p != null) {
            e.send(new EmbedBuilder()
                .setTitle(p.getName())
                .setDescription("General Info:" +
                    "\n**Type**: "+p.getType().getName() +
                    "\n**Base Stats**: "+p.getBaseStats() +
                    "\n**EV Yield**: "+p.getEvYield()+
                    "\n**Base Exp Rate**: "+p.getExpYield())
                .addField("Possible Abilities", p.getAbilities()
                    .stream()
                    .map(slot -> slot.getAbility().getName() + (slot.isHidden() ? " (Hidden)" : ""))
                    .collect(Collectors.joining(", ")), false)
//                .addField("Moveset", p.getMoveset()
//                    .entrySet().stream()
//                    .sorted(Comparator.comparing(entry -> entry.getValue().get(0).getMethod()))
//                    .map(entry -> entry.getKey() + " " + entry.getValue()
//                        .stream()
//                        .map(Object::toString)
//                        .collect(Collectors.joining(", and ")))
//                    .collect(Collectors.joining("\n")), false)
                .build(), toSearch).queue();
            return;
        }
        Move m = Bot.moveMap.get(toSearch);
        if (m != null) {

        }
    }
}
