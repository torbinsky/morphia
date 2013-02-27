package com.github.torbinsky.morphia.query;

import com.mongodb.DBObject;

public interface Criteria {
    void addTo(DBObject obj);

    void attach(CriteriaContainerImpl container);

    String getFieldName();
}
