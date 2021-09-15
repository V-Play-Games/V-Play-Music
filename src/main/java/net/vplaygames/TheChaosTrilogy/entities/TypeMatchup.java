package net.vplaygames.TheChaosTrilogy.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TypeMatchup {
    Map<String, Double> matchup = new HashMap<>();

    public TypeMatchup put(String type, double multiplier) {
        matchup.put(type, multiplier);
        return this;
    }

    public double effectivenessAgainst(String type) {
        return matchup.getOrDefault(type, 1.0);
    }

    public Map<String, Double> getMap() {
        return matchup;
    }

    public List<Type> filterEquals(double multiplier) {
        return matchup.entrySet()
            .stream()
            .filter(e -> e.getValue() == multiplier)
            .map(Map.Entry::getKey)
            .map(SingleType::fromId)
            .collect(Collectors.toList());
    }

    public List<Type> filterMoreThan(double multiplier) {
        return matchup.entrySet()
            .stream()
            .filter(e -> e.getValue() > multiplier)
            .map(Map.Entry::getKey)
            .map(SingleType::fromId)
            .collect(Collectors.toList());
    }

    public List<Type> filterLessThan(double multiplier) {
        return matchup.entrySet()
            .stream()
            .filter(e -> e.getValue() < multiplier)
            .map(Map.Entry::getKey)
            .map(SingleType::fromId)
            .collect(Collectors.toList());
    }
}
