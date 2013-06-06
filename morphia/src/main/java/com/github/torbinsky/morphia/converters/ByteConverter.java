/**
 *
 */
package com.github.torbinsky.morphia.converters;

import com.github.torbinsky.morphia.mapping.MappedField;
import com.github.torbinsky.morphia.mapping.MappingException;
import org.bson.types.Binary;


/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
@SuppressWarnings({"rawtypes"})
public class ByteConverter extends TypeConverter implements SimpleValueConverter {
    public ByteConverter() {
        super(Byte.class, byte.class);
    }

    @Override
    public Object decode(Class targetClass, Object val, MappedField optionalExtraInfo) throws MappingException {
        if (val == null) return null;

        if (val instanceof Number)
            return ((Number) val).byteValue();
        
        if (val instanceof Binary) {
        	return ((Binary)val).getData();
        }
        
        String sVal = val.toString();
        return Byte.parseByte(sVal);
    }
}
