package net.vplaygames.TheChaosTrilogy.core;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Player {
    public static final Pattern referencePattern = Pattern.compile("\\$\\{([A-Za-z-]+)}");
    public static final Pattern genderBasedTextSplit = Pattern.compile("\\{m:(.+);f:(.+)}");
    public long id;
    // -1 if not set, 0 for female, 1 for male
    private int male;
    private String position;
    private Map<String, String> properties;

    public Player(long id) {
        this.id = id;
        this.male = -1;
        this.properties = new HashMap<>();
        Bot.players.put(id, this);
    }

    public int getMale() {
        return male;
    }

    public boolean isMale() {
        return male == 1;
    }

    public Player setMale(int male) {
        this.male = male;
        return this;
    }

    public String getPosition() {
        return position;
    }

    public Player setPosition(String position) {
        this.position = position;
        setProperty("position", position);
        return this;
    }

    public long getId() {
        return id;
    }

    public String getMention() {
        return "<@" + id + ">";
    }

    public String resolveReferences(String s) {
        s = Util.replaceAll(referencePattern, s, m -> getProperty(m.group(1)));
        s = Util.replaceAll(genderBasedTextSplit, s, m -> this.isMale() ? m.group(1) : m.group(2));
        return s;
    }

    public String getProperty(String key) {
        switch (key) {
            case "rival":
                return this.isMale() ? "Amy" : "Toby";
            case "heshe":
                return this.isMale() ? "he" : "she";
            case "himher":
                return this.isMale() ? "him" : "her";
            case "user":
                return this.getMention();
            case "user-id":
                return String.valueOf(id);
            default:
                return properties.getOrDefault(key, "null");
        }
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }
}
