package net.vplaygames.TheChaosTrilogy.entities;

public enum Ailment {
    UNKNOWN("Unknown"),
    NONE("None"),
    PARALYSIS("paralyzed"),
    SLEEP("asleep"),
    FREEZE("frozen"),
    BURN("burnt"),
    POISON("poisoned"),
    BADLY_POISON("badly poisoned"),
    CONFUSION("confused"),
    INFATUATION("infatuated"),
    TRAP("trapped"),
    NIGHTMARE("nightmare"),
    TORMENT("tormented"),
    DISABLE("disabled"),
    YAWN("yawning"),
    SILENCE("silenced"),
    HEAL_BLOCK("heal blocked"),
    NO_TYPE_IMMUNITY("no type immunity"),
    LEECH_SEED("leech seeded"),
    EMBARGO("embargo"),
    PERISH_SONG("Perish Song"),
    INGRAIN("Ingrain"),
    TELEKINESIS("Telekinesis"),
    NO_GROUND_IMMUNITY("No Ground Immunity"),
    ;

    String key;
    String name;

    Ailment(String name) {
        this.name = name;
        this.key = toString().toLowerCase().replaceAll("_", "-");
    }

    public static Ailment fromKey(String key) {
        for (Ailment ailment : values()) {
            if (ailment.getKwy().equals(key)) {
                return ailment;
            }
        }
        throw new RuntimeException(key + " ailment?");
    }

    public String getName() {
        return name;
    }

    public String getKwy() {
        return key;
    }
}
