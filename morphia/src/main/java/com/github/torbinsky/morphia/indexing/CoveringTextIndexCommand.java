package com.github.torbinsky.morphia.indexing;

import java.util.Map;

import com.github.torbinsky.morphia.utils.IndexDirection;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * A way of creating an index to allow a covering query using a text search.
 */
public class CoveringTextIndexCommand extends TextIndexCommand {

    private Map<String, IndexDirection> coveringFields;

    public void setCoveringFields(Map<String, IndexDirection> coveringFields) {
        this.coveringFields = coveringFields;
    }

    public DBObject getKeys() {
        BasicDBObjectBuilder builder = BasicDBObjectBuilder.start(super.getKeys().toMap());

        for (Map.Entry<String, IndexDirection> entry : coveringFields.entrySet()) {
            builder.add(entry.getKey(), entry.getValue().toIndexValue());
        }

        return builder.get();
    }
}
