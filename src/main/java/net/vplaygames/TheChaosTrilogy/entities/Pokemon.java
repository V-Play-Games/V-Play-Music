package net.vplaygames.TheChaosTrilogy.entities;

import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.vplaygames.TheChaosTrilogy.core.Bot;
import net.vplaygames.TheChaosTrilogy.core.Util;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Pokemon implements Entity {
    List<AbilitySlot> abilities;
    boolean isDefault;
    String species;
    Stats evYield;
    Stats baseStats;
    Type type;
    String id;
    int expYield;
    List<String> forms;
    String name;
    Map<String, List<MoveLearningMethod>> moveset;

    public Pokemon(DataObject data) {
        abilities = data.getArray("abilities")
            .stream(DataArray::getObject)
            .map(AbilitySlot::new)
            .collect(Collectors.toList());
        isDefault = data.getBoolean("default");
        species = data.getString("species");
        evYield = new Stats(data.getObject("ev_yield"));
        baseStats = new Stats(data.getObject("stats"));
        type = Type.fromId(data.getString("type"));
        id = data.getString("name");
        name = Util.toProperCase(String.join(" ", id.split("-")));
        expYield = data.getInt("exp");
        forms = data.getArray("forms").stream(DataArray::getString).collect(Collectors.toList());
        moveset = data.getObject("moves")
            .toMap()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> Arrays.stream(e.getValue().toString().split(";;;"))
            .map(MoveLearningMethod::of)
            .collect(Collectors.toList())));
    }

    public static EntityInitInfo<Pokemon> getInfo() {
        return new EntityInitInfo<>(new File(Ability.class.getResource("pokemon.json").toString()),
            Pokemon::new, Bot.pokemonMap);
    }

    public List<AbilitySlot> getAbilities() {
        return abilities;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public String getSpecies() {
        return species;
    }

    public Stats getEvYield() {
        return evYield;
    }

    public Stats getBaseStats() {
        return baseStats;
    }

    public Type getType() {
        return type;
    }

    public int getExpYield() {
        return expYield;
    }

    public List<String> getForms() {
        return forms;
    }

    public String getName() {
        return name;
    }

    public Map<String, List<MoveLearningMethod>> getMoveset() {
        return moveset;
    }

    @Override
    public String getId() {
        return id;
    }

    public static class AbilitySlot {
        boolean hidden;
        EntityReference<Ability> reference;
        int slot;
        Ability cachedAbility;

        public AbilitySlot(DataObject data) {
            this(data.getBoolean("is_hidden"), data.getString("ability"), data.getInt("slot"));
        }

        public AbilitySlot(boolean hidden, String abilityId, int slot) {
            this.hidden = hidden;
            this.reference = new EntityReference<>(Ability.class, abilityId);
            this.slot = slot;
        }

        public String getId() {
            return getAbility().getId();
        }

        public Ability getAbility() {
            return cachedAbility == null ? cachedAbility = reference.get() : cachedAbility;
        }

        public boolean isHidden() {
            return hidden;
        }

        public int getSlot() {
            return slot;
        }
    }

    public static class MoveLearningMethod {
        static Map<String, MoveLearningMethod> methods = new HashMap<>();

        static {
            for (int i = 0; i <= 100; i++) {
                methods.put("level-up;" + i, new MoveLearningMethod("level-up", i));
            }
            methods.put("machine;0", new MoveLearningMethod("machine", 0));
            methods.put("egg;0", new MoveLearningMethod("egg", 0));
            methods.put("tutor;0", new MoveLearningMethod("egg", 0));
            methods.put("form-change;0", new MoveLearningMethod("form-change", 0));
            methods.put("light-ball-egg;0", new MoveLearningMethod("light-ball-egg", 0));
        }

        String method;
        int levelRequired;

        MoveLearningMethod(String method, int levelRequired) {
            this.method = method;
            this.levelRequired = levelRequired;
        }

        public static MoveLearningMethod of(String id) {
            return Optional.ofNullable(methods.get(id)).orElseThrow(() -> new RuntimeException(id));
        }

        public String getMethod() {
            return method;
        }

        public String toString() {
            switch (method) {
                case "level-up":
                    return "at level "+levelRequired;
                case "machine":
                    return "TM/HM";
                case "egg":
                    return "by breeding";
                case "tutor":
                    return "by move tutor";
                case "form-change":
                    return "after changing form";
                case "light-ball-egg":
                    return "using what?";
            }
            return "by no idea how";
        }

        public int getLevelRequired() {
            return levelRequired;
        }
    }
}
