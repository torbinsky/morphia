/**
 * 
 */
package com.github.jmkgreen.morphia.converters;

import java.util.UUID;

import com.github.jmkgreen.morphia.mapping.MappedField;
import com.github.jmkgreen.morphia.mapping.MappingException;

/**
 * provided by http://code.google.com/p/morphia/issues/detail?id=320
 * @author stummb
 * @author scotthernandez
 */
@SuppressWarnings({ "rawtypes" })
public class UUIDConverter extends TypeConverter implements
		SimpleValueConverter {

	public UUIDConverter() {
		super(UUID.class);
	}

	public Object encode(Object value, MappedField optionalExtraInfo) {
		UUID uuid = (UUID) value;
		return uuid == null ? null : uuid.toString();
	}

	public Object decode(Class targetClass, Object fromDBObject, MappedField optionalExtraInfo) throws MappingException {
		String uuidString = (String) fromDBObject;
		return uuidString == null ? null : UUID.fromString(uuidString);
	}
}