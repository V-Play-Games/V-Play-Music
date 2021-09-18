package net.vplaygames.TheChaosTrilogy.entities;

import net.dv8tion.jda.api.utils.data.DataObject;
import net.vplaygames.TheChaosTrilogy.core.Bot;
import net.vplaygames.TheChaosTrilogy.core.Util;

import java.io.File;
import java.net.URISyntaxException;

public class Move implements Entity {
    int pp;
    int accuracy;
    String description;
    int priority;
    Type type;
    Target target;
    String effect;
    String name;
    String id;
    int effectChance;
    int power;
    Category category;
    Metadata metadata;

    public Move(DataObject data) {
        this.description = data.getString("description");
        this.effect = data.getString("effect");
        this.name = data.getString("name");
        this.id = data.getString("id");
        this.pp = data.getInt("pp", -1);
        this.accuracy = data.getInt("accuracy", -1);
        this.priority = data.getInt("priority", -1);
        this.effectChance = data.getInt("effectChance", -1);
        this.power = data.getInt("power", -1);
        this.type = Type.fromId(data.getString("type"));
        this.target = Target.fromKey(data.getString("target"));
        this.category = Category.fromKey(data.getString("category"));
        this.metadata = new Metadata(data.getObject("meta"));
    }

    public static EntityInitInfo<Move> getInfo() throws URISyntaxException {
        return new EntityInitInfo<>(new File(Ability.class.getResource("move.json").toURI()),
            Move::new, Bot.moveMap);
    }

    public int getPP() {
        return pp;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public Type getType() {
        return type;
    }

    public Target getTarget() {
        return target;
    }

    public String getEffect() {
        return effect;
    }

    public String getName() {
        return name;
    }

    public int getEffectChance() {
        return effectChance;
    }

    public int getPower() {
        return power;
    }

    public Category getCategory() {
        return category;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public String getId() {
        return id;
    }

    public enum Target {
        ALLY("Ally"),
        USER("User"),
        USER_OR_ALLY("User or Ally"),
        USER_AND_ALLIES("User and Allies"),
        SELECTED_POKEMON("Selected Pokemon"),
        SELECTED_POKEMON_ME_FIRST("Selected Pokemon (Me First)"),
        RANDOM_OPPONENT("Random Opponent"),
        ENTIRE_FIELD("Entire Field"),
        USERS_FIELD("User's Field"),
        OPPONENTS_FIELD("Opponents' Field"),
        ALL_OPPONENTS("All Opponents"),
        ALL_OTHER_POKEMON("All Other Pokemon"),
        ALL_POKEMON("All Pokemon"),
        ALL_ALLIES("All Allies"),
        SPECIFIC_MOVE("Specific Move"),
        ;

        String key;
        String name;

        Target(String name) {
            this.name = name;
            this.key = toString().toLowerCase().replaceAll("_", "-");
        }

        public static Target fromKey(String key) {
            for (Target target : values()) {
                if (target.getKwy().equals(key)) {
                    return target;
                }
            }
            throw new RuntimeException(key + " target?");
        }

        public String getName() {
            return name;
        }

        public String getKwy() {
            return key;
        }
    }

    public enum EffectCategory {
        FORCE_SWITCH("Force Switch", "force-switch"),
        DAMAGE("Damage", "damage"),
        DAMAGE_AND_AILMENT("Damage + Ailment", "damage+ailment"),
        DAMAGE_AND_LOWER_STATS("Damage + Lower Stats", "damage+lower"),
        DAMAGE_AND_RAISE_STATS("Damage + Raise Stats", "damage+raise"),
        DAMAGE_AND_HEAL("Damage + Heal", "damage+heal"),
        OHKO("One-hit KO (OHKO)", "ohko"),
        AILMENT("Ailment", "ailment"),
        HEAL("Heal", "heal"),
        NET_GOOD_STATS("Net Good Stats", "net-good-stats"),
        UNIQUE("Unique", "unique"),
        SWAGGER("Swagger", "swagger"),
        WHOLE_FIELD_EFFECT("Whole Field Effect", "whole-field-effect"),
        FIELD_EFFECT("Field Effect", "field-effect"),
        ;

        String key;
        String name;

        EffectCategory(String name, String key) {
            this.name = name;
            this.key = key;
        }

        public static EffectCategory fromKey(String key) {
            for (EffectCategory category : values()) {
                if (category.getKwy().equals(key)) {
                    return category;
                }
            }
            throw new RuntimeException(key + " category?");
        }

        public String getName() {
            return name;
        }

        public String getKwy() {
            return key;
        }
    }

    public enum Category {
        PHYSICAL,
        SPECIAL,
        STATUS,
        ;

        String key;
        String name;

        Category() {
            this.name = Util.toProperCase(toString());
            this.key = toString().toLowerCase();
        }

        public static Category fromKey(String key) {
            for (Category category : values()) {
                if (category.getKwy().equals(key)) {
                    return category;
                }
            }
            throw new RuntimeException(key + " category?");
        }

        public String getName() {
            return name;
        }

        public String getKwy() {
            return key;
        }
    }

    public static class Metadata {
        int healing;
        int minHits;
        int maxHits;
        int minTurns;
        int maxTurns;
        int critRate;
        int drain;
        int ailmentChance;
        int flinchChance;
        int statChance;
        Ailment ailment;
        EffectCategory category;

        public Metadata(DataObject data) {
            this.healing = data.getInt("healing", -1);
            this.minHits = data.getInt("min_hits", -1);
            this.maxHits = data.getInt("max_hits", -1);
            this.minTurns = data.getInt("min_turns", -1);
            this.maxTurns = data.getInt("max_turns", -1);
            this.critRate = data.getInt("crit_rate", -1);
            this.drain = data.getInt("drain", -1);
            this.ailmentChance = data.getInt("ailment_chance", -1);
            this.flinchChance = data.getInt("flinch_chance", -1);
            this.statChance = data.getInt("stat_chance", -1);
            this.ailment = Ailment.fromKey(data.getString("ailment"));
            this.category = EffectCategory.fromKey(data.getString("category"));
        }

        public int getHealing() {
            return healing;
        }

        public int getMinHits() {
            return minHits;
        }

        public int getMaxHits() {
            return maxHits;
        }

        public int getMinTurns() {
            return minTurns;
        }

        public int getMaxTurns() {
            return maxTurns;
        }

        public int getCritRate() {
            return critRate;
        }

        public int getDrain() {
            return drain;
        }

        public int getAilmentChance() {
            return ailmentChance;
        }

        public int getFlinchChance() {
            return flinchChance;
        }

        public int getStatChance() {
            return statChance;
        }

        public Ailment getAilment() {
            return ailment;
        }

        public EffectCategory getCategory() {
            return category;
        }
    }
}
