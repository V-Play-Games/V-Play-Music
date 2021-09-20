package net.vplaygames.vpm.entities;

import net.dv8tion.jda.api.utils.data.DataObject;

import java.net.URL;
import java.util.Map;
import java.util.function.Function;

public class EntityInitInfo<T extends Entity> {
    public final URL fileUrl;
    public final Function<DataObject, T> entityConstructor;
    public final Map<String, T> entityMap;

    public EntityInitInfo(URL fileUrl, Function<DataObject, T> entityConstructor, Map<String, T> entityMap) {
        this.fileUrl = fileUrl;
        this.entityConstructor = entityConstructor;
        this.entityMap = entityMap;
    }
}
