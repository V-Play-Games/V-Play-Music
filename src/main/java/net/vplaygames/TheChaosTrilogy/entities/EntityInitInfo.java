package net.vplaygames.TheChaosTrilogy.entities;

import net.dv8tion.jda.api.utils.data.DataObject;

import java.io.File;
import java.util.Map;
import java.util.function.Function;

public class EntityInitInfo<T extends Entity> {
    public final File entityFile;
    public final Function<DataObject, T> entityConstructor;
    public final Map<String, T> entityMap;

    public EntityInitInfo(File entityFile, Function<DataObject, T> entityConstructor, Map<String, T> entityMap) {
        this.entityFile = entityFile;
        this.entityConstructor = entityConstructor;
        this.entityMap = entityMap;
    }
}
