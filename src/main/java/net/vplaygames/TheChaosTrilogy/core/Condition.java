package net.vplaygames.TheChaosTrilogy.core;

import java.util.List;

public class Condition {
    public static boolean doesFulfill(List<String> conditions, Player player) {
        return conditions.stream()
            .map(s -> {
                String method = Util.getMethod(s);
                String[] args = Util.getArgs(s).split(";");
                switch (method) {
                    case "equals":
                        return player.resolveReferences(args[0]).equals(player.resolveReferences(args[1]));
                    case "return":
                        return Boolean.parseBoolean(args[0]);
                    default:
                        return false;
                }
            })
            .reduce(Boolean::logicalAnd)
            .orElse(false);
    }
}
