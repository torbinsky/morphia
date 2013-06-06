package com.github.torbinsky.morphia.indexing;

import java.util.Map;

import com.github.torbinsky.morphia.utils.IndexDirection;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * A way to create an index suitable for use with text searches that specify a filter.
 */
public class FilteredTextIndexCommand extends TextIndexCommand {

    private Map<String, IndexDirection> filteredFields;

    public void setFilteredFields(Map<String, IndexDirection> filteredFields) {
        this.filteredFields = filteredFields;
    }

    public DBObject getKeys() {
        BasicDBObjectBuilder keys = new BasicDBObjectBuilder();
        for (Map.Entry<String, IndexDirection> entry : filteredFields.entrySet()) {
            keys.add(entry.getKey(), entry.getValue().toIndexValue());
        }

        DBObject compoundFields = keys.get();

        compoundFields.putAll(super.getKeys());
        return compoundFields;
    }
}
