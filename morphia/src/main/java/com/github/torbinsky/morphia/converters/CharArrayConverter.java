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
@SuppressWarnings({"rawtypes"})
public class CharArrayConverter extends TypeConverter implements SimpleValueConverter {
    public CharArrayConverter() {
        super(char[].class);
    }

    @Override
    public Object decode(Class targetClass, Object fromDBObject, MappedField optionalExtraInfo) throws MappingException {
        if (fromDBObject == null) return null;

        return fromDBObject.toString().toCharArray();
    }

    @Override
    public Object encode(Object value, MappedField optionalExtraInfo) {
        return new String((char[]) value);
    }
}
