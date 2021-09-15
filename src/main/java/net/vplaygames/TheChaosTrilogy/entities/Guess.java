package net.vplaygames.TheChaosTrilogy.entities;

import net.dv8tion.jda.api.utils.data.DataObject;
import net.vplaygames.TheChaosTrilogy.core.Bot;

import java.io.File;

public class Guess implements Entity {
    private String id;
    private String name;
    private String description;

    public Guess(DataObject data) {
        this.id = data.getString("id");
        this.name = data.getString("name");
        this.description = data.getString("description");
    }

    public static EntityInitInfo<Guess> getInfo() {
        return new EntityInitInfo<>(new File("src/main/resources/guess.json"), Guess::new, Bot.guessMap);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

