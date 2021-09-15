package net.vplaygames.TheChaosTrilogy.entities;

import java.util.List;

public interface Type {
    static Type fromId(String id) {
        return id.contains("-") ? DualType.fromId(id) : SingleType.fromId(id);
    }

    String getName();

    String getId();

    List<Type> immuneTo();

    List<Type> effectiveAgainst();

    TypeMatchup getMatchup();

    default double effectivenessAgainst(String type) {
        return getMatchup().effectivenessAgainst(type);
    }

    double multiplierReceivingDamage(Type type);
}
