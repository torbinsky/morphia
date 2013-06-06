package com.github.torbinsky.morphia.utils;

import com.github.torbinsky.morphia.Datastore;
import com.github.torbinsky.morphia.query.MorphiaIterator;
import com.github.torbinsky.morphia.query.Query;
import com.github.torbinsky.morphia.query.QueryImpl;
import com.github.torbinsky.morphia.query.UpdateOperations;
import com.github.torbinsky.morphia.query.UpdateOpsImpl;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * Exposes driver related DBOBject stuff from Morphia objects
 *
 * @author scotthernandez
 */
@SuppressWarnings("rawtypes")
public class Helper {
    public static DBObject getCriteria(Query q) {
        QueryImpl qi = (QueryImpl) q;
        return qi.getQueryObject();
    }

    public static DBObject getSort(Query q) {
        return q.getSortObject();
    }

    public static DBObject getFields(Query q) {
        return q.getFieldsObject();
    }

    public static DBCollection getCollection(Query q) {
        return q.getCollection();
    }

    public static DBCursor getCursor(Iterable it) {
        return ((MorphiaIterator) it).getCursor();
    }

    public static DBObject getUpdateOperations(UpdateOperations ops) {
        UpdateOpsImpl uo = (UpdateOpsImpl) ops;
        return uo.getOps();
    }

    public static DB getDB(Datastore ds) {
        return ds.getDB();
    }
}