package net.vplaygames.TheChaosTrilogy.core;

import java.util.List;

public class Condition {
    public static boolean doesFulfill(List<String> conditions, Player player) {
        return conditions.stream()
            .map(s -> {
                boolean tor = false;
                String method = Util.getMethod(s);
                String[] args = Util.getArgs(s).split(";");
                switch (method) {
                    case "equals":
                        tor = player.resolveReferences(args[0]).equals(player.resolveReferences(args[1]));
                        break;
                    case "return":
                        tor = Boolean.parseBoolean(args[0]);
                    default:
                        tor = false;
                }
                return tor;
            })
            .reduce(Boolean::logicalAnd)
            .orElse(false);
    }
}
