package net.vplaygames.TheChaosTrilogy.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DualType implements Type {
    static Map<String, Map<String, DualType>> typeMap = new HashMap<>();

    static {
        for (SingleType primary : SingleType.values()) {
            typeMap.put(primary.getId(), new HashMap<>());
            for (SingleType secondary : SingleType.values()) {
                typeMap.get(primary.getId()).put(secondary.getId(), new DualType(primary, secondary));
            }
        }
    }

    SingleType primary;
    SingleType secondary;
    TypeMatchup matchup;
    List<Type> immune;
    List<Type> effective;

    DualType(SingleType primary, SingleType secondary) {
        this.primary = primary;
        this.secondary = secondary;
        matchup = new TypeMatchup();
        for (Type type : SingleType.values()) {
            matchup.put(type.getId(), primary.getMatchup().effectivenessAgainst(type.getId()) * secondary.getMatchup().effectivenessAgainst(type.getId()));
        }
        immune = matchup.filterEquals(0);
        effective = matchup.filterMoreThan(0);
    }

    public static DualType fromId(String id) {
        String[] types = id.split("-");
        assert types.length == 2;
        return typeMap.get(types[0]).get(types[1]);
    }

    @Override
    public String getName() {
        return primary.getName() + "/" + secondary.getName();
    }

    @Override
    public String getId() {
        return primary.getId() + "-" + secondary.getId();
    }

    @Override
    public List<Type> immuneTo() {
        return immune;
    }

    @Override
    public List<Type> effectiveAgainst() {
        return effective;
    }

    @Override
    public TypeMatchup getMatchup() {
        return matchup;
    }

    @Override
    public double multiplierReceivingDamage(Type type) {
        return primary.multiplierReceivingDamage(type) * secondary.multiplierReceivingDamage(type);
    }
}
