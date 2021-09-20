package net.vplaygames.vpm.entities;

import net.vplaygames.vpm.core.Bot;

import java.util.Map;

public class EntityReference<T extends Entity> {
    public final Map<String, ?> map;
    public final String id;

    public EntityReference(Class<T> entityClass, String id) {
        this.id = id;
        this.map = Bot.initInfoMap.get(entityClass.getName()).entityMap;
    }

    @SuppressWarnings("unchecked")
    public T get() {
        return (T) map.get(id);
    }

    public String getId() {
        return id;
    }
}
