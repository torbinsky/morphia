package com.github.torbinsky.morphia.query;

import com.github.torbinsky.morphia.Key;
import com.github.torbinsky.morphia.mapping.Mapper;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author Scott Hernandez
 */
public class MorphiaKeyIterator<T> extends MorphiaIterator<T, Key<T>> {
    public MorphiaKeyIterator(DBCursor cursor, Mapper m, Class<T> clazz, String kind) {
        super(cursor, m, clazz, kind, null);
    }

    @Override
    protected Key<T> convertItem(DBObject dbObj) {
        Key<T> key = new Key<T>(kind, dbObj.get(Mapper.ID_KEY));
        key.setKindClass(this.clazz);
        return key;
    }

}