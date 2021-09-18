package net.vplaygames.TheChaosTrilogy.entities;

import net.dv8tion.jda.api.utils.data.DataObject;
import net.vplaygames.TheChaosTrilogy.core.Bot;
import net.vplaygames.TheChaosTrilogy.core.Util;

import java.io.File;

public class Ability implements Entity {
    String id;
    String name;
    String effect;
    String description;

    public Ability(DataObject data) {
        this.id = data.getString("name");
        this.name = Util.toProperCase(String.join(" ", id.split("-")));
        this.effect = data.getString("effect");
        this.description = data.getString("description");
    }

    public static EntityInitInfo<Ability> getInfo() {
        return new EntityInitInfo<>(new File(Ability.class.getResource("ability.json").toString()),
            Ability::new, Bot.abilityMap);
    }

    public String getName() {
        return name;
    }

    public String getEffect() {
        return effect;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String getId() {
        return id;
    }
}
