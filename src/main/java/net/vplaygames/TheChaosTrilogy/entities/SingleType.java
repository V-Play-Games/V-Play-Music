package net.vplaygames.TheChaosTrilogy.entities;

import net.vplaygames.TheChaosTrilogy.core.Util;

import java.util.List;

public enum SingleType implements Type {
    NORMAL(new TypeMatchup()
        .put("rock", 0.5)
        .put("ghost", 0)
        .put("steel", 0.5)),
    FIRE(new TypeMatchup()
        .put("fire", 0.5)
        .put("water", 0.5)
        .put("grass", 2)
        .put("ice", 2)
        .put("bug", 2)
        .put("rock", 0.5)
        .put("dragon", 0.5)
        .put("steel", 2)),
    WATER(new TypeMatchup()
        .put("fire", 2)
        .put("water", 0.5)
        .put("grass", 0.5)
        .put("ground", 2)
        .put("rock", 2)
        .put("dragon", 0.5)),
    ELECTRIC(new TypeMatchup()
        .put("water", 2)
        .put("electric", 0.5)
        .put("grass", 0.5)
        .put("ground", 0)
        .put("flying", 2)
        .put("dragon", 0.5)),
    GRASS(new TypeMatchup()),
    ICE(new TypeMatchup()),
    FIGHTING(new TypeMatchup()),
    POISON(new TypeMatchup()),
    GROUND(new TypeMatchup()),
    FLYING(new TypeMatchup()),
    PSYCHIC(new TypeMatchup()),
    BUG(new TypeMatchup()),
    ROCK(new TypeMatchup()),
    GHOST(new TypeMatchup()
        .put("normal", 0)
        .put("ghost", 2)
        .put("dark", 0.5)
        .put("psychic", 2)),
    DRAGON(new TypeMatchup()),
    DARK(new TypeMatchup()),
    STEEL(new TypeMatchup()),
    FAIRY(new TypeMatchup());

    TypeMatchup matchup;
    List<Type> immune;
    List<Type> effective;

    SingleType(TypeMatchup matchup) {
        this.matchup = matchup;
    }

    public static SingleType fromId(String id) {
        for (SingleType type : values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        throw new IllegalArgumentException(id + " type?");
    }

    @Override
    public String getName() {
        return Util.toProperCase(toString());
    }

    @Override
    public String getId() {
        return toString().toLowerCase();
    }

    @Override
    public List<Type> immuneTo() {
        return immune == null ? immune = matchup.filterEquals(0) : immune;
    }

    @Override
    public List<Type> effectiveAgainst() {
        return effective == null ? effective = matchup.filterMoreThan(0) : effective;
    }

    @Override
    public TypeMatchup getMatchup() {
        return matchup;
    }

    @Override
    public double multiplierReceivingDamage(Type type) {
        return type.getMatchup().effectivenessAgainst(this.getId());
    }
}
