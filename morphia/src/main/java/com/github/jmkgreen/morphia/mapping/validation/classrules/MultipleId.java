/**
 * 
 */
package com.github.jmkgreen.morphia.mapping.validation.classrules;

import java.util.List;
import java.util.Set;

import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.mapping.MappedClass;
import com.github.jmkgreen.morphia.mapping.MappedField;
import com.github.jmkgreen.morphia.mapping.validation.ClassConstraint;
import com.github.jmkgreen.morphia.mapping.validation.ConstraintViolation;
import com.github.jmkgreen.morphia.mapping.validation.ConstraintViolation.Level;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class MultipleId implements ClassConstraint {
	
	public void check(MappedClass mc, Set<ConstraintViolation> ve) {
		
		List<MappedField> idFields = mc.getFieldsAnnotatedWith(Id.class);
		
		if (idFields.size() > 1) {
			ve.add(new ConstraintViolation(Level.FATAL, mc, this.getClass(), "More than one @" + Id.class.getSimpleName()
					+ " Field found (" + new FieldEnumString(idFields)
					+ ")."));
		}
	}
	
}
