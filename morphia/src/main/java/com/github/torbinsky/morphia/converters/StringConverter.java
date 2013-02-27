/**
 *
 */
package com.github.torbinsky.morphia.converters;

import com.github.torbinsky.morphia.mapping.MappedField;
import com.github.torbinsky.morphia.mapping.MappingException;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class StringConverter extends TypeConverter implements SimpleValueConverter {
    public StringConverter() {
        super(String.class);
    }

    @Override
    public Object decode(Class targetClass, Object fromDBObject, MappedField optionalExtraInfo) throws MappingException {
        if (fromDBObject == null) return null;

        if (fromDBObject instanceof String)
            return (String) fromDBObject;

        return fromDBObject.toString();
    }
}
