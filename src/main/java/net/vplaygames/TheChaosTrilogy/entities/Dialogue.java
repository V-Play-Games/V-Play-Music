package net.vplaygames.TheChaosTrilogy.entities;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.vplaygames.TheChaosTrilogy.core.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class Dialogue implements Entity {
    private String id;
    private Map<String, State> states;

    public Dialogue(DataObject data) {
        this.id = data.getString("id");
        states = data.getArray("states")
            .stream(DataArray::getObject)
            .map(State::new)
            .collect(Collectors.toMap(State::getId, UnaryOperator.identity()));
    }

    public static EntityInitInfo<Dialogue> getInfo() throws URISyntaxException {
        return new EntityInitInfo<>(new File(Ability.class.getResource("dialogue.json").toURI()),
            Dialogue::new, Bot.dialogueMap);
    }

    public String getId() {
        return id;
    }

    public String getText(Player player) {
        return getStateFor(player).getText(player);
    }

    public void send(CommandReceivedEvent e) {
        getStateFor(e.getAuthor().getIdLong()).send(e);
    }

    public void send(ButtonClickEvent e) {
        getStateFor(e.getUser().getIdLong()).send(e);
    }

    public State getState(String id) {
        return states.get(id);
    }

    public State getStateFor(long id) {
        return getStateFor(Bot.getPlayer(id));
    }

    public State getStateFor(Player player) {
        return states.values()
            .stream()
            .sorted(Comparator.comparingInt(State::getPriority))
            .filter(state -> Condition.doesFulfill(state.conditions, player))
            .findFirst()
            .orElseThrow(RuntimeException::new);
    }

    public void executeActions(ButtonClickEvent e, String stateId, String actionId) {
        getState(stateId).executeActions(e, actionId);
    }

    public class State {
        private String id;
        private int priority;
        private List<String> conditions;
        private List<String> interaction_actions;
        private String text;
        private Map<String, List<String>> buttonActions;
        private List<Button> buttons;

        public State(DataObject data) {
            this.id = data.getString("id");
            this.text = data.getString("text");
            this.priority = data.getInt("priority");
            this.conditions = Util.list(data.getString("conditions").split(";;;"));
            this.interaction_actions = data.optArray("interaction-actions")
                .map(array -> array.stream(DataArray::getString)
                    .collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
            this.buttonActions = new HashMap<>();
            this.buttons = new ArrayList<>();
            data.getArray("buttons")
                .stream(DataArray::getObject)
                .forEach(button -> {
                    String id = button.getString("id");
                    buttonActions.put(id, button.getArray("actions")
                        .stream(DataArray::getString)
                        .collect(Collectors.toList()));
                    if (button.hasKey("emote")) {
                        buttons.add(Button.primary(id, Emoji.fromMarkdown(button.getString("emote"))));
                    } else {
                        buttons.add(Button.primary(id, button.getString("label", "wha-")));
                    }
                });
        }

        public Dialogue getParent() {
            return Dialogue.this;
        }

        public String getId() {
            return id;
        }

        public String getText(Player player) {
            return player.resolveReferences(text);
        }

        public void send(CommandReceivedEvent e) {
            long id = e.getAuthor().getIdLong();
            e.send(getText(Bot.getPlayer(id).setPosition(Dialogue.this.id)))
                .addActionRows(getActionRow(id))
                .queue();
        }

        public void send(ButtonClickEvent e) {
            long id = e.getUser().getIdLong();
            e.reply(getText(Bot.getPlayer(id).setPosition(Dialogue.this.id)))
                .addActionRows(getActionRow(id))
                .queue();
        }

        public List<String> getButtonActions(String actionId) {
            return buttonActions.get(actionId);
        }

        public void executeActions(ButtonClickEvent e, String actionId) {
            getButtonActions(actionId).forEach(s -> Bot.actionHandlers.get(Util.getMethod(s)).handle(e, Util.getArgs(s)));
            interaction_actions.forEach(s -> Bot.actionHandlers.get(Util.getMethod(s)).handle(e, Util.getArgs(s)));
        }

        public ActionRow getActionRow(long userId) {
            return ActionRow.of(buttons.stream().map(b -> b.withId("area:" + Dialogue.this.id + ":" + this.id + ":" + b.getId() + ":" + userId)).collect(Collectors.toList()));
        }

        public List<String> getConditions() {
            return conditions;
        }

        public int getPriority() {
            return priority;
        }
    }
}
