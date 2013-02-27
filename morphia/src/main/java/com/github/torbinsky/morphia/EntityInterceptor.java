package com.github.torbinsky.morphia;

import com.github.torbinsky.morphia.annotations.PostLoad;
import com.github.torbinsky.morphia.annotations.PostPersist;
import com.github.torbinsky.morphia.annotations.PreLoad;
import com.github.torbinsky.morphia.annotations.PrePersist;
import com.github.torbinsky.morphia.annotations.PreSave;
import com.github.torbinsky.morphia.mapping.Mapper;
import com.mongodb.DBObject;

/**
 * Interface for intercepting @Entity lifecycle events.
 */
public interface EntityInterceptor {
    /**
     * see {@link PrePersist}
     */
    void prePersist(Object ent, DBObject dbObj, Mapper mapr);

    /**
     * see {@link PreSave}
     */
    void preSave(Object ent, DBObject dbObj, Mapper mapr);

    /**
     * see {@link PostPersist}
     */
    void postPersist(Object ent, DBObject dbObj, Mapper mapr);

    /**
     * see {@link PreLoad}
     */
    void preLoad(Object ent, DBObject dbObj, Mapper mapr);

    /**
     * see {@link PostLoad}
     */
    void postLoad(Object ent, DBObject dbObj, Mapper mapr);
}
