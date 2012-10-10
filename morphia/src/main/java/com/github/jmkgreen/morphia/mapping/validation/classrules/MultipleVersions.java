/**
 * 
 */
package com.github.jmkgreen.morphia.mapping.validation.classrules;

import java.util.List;
import java.util.Set;

import com.github.jmkgreen.morphia.annotations.Version;
import com.github.jmkgreen.morphia.mapping.MappedClass;
import com.github.jmkgreen.morphia.mapping.MappedField;
import com.github.jmkgreen.morphia.mapping.validation.ClassConstraint;
import com.github.jmkgreen.morphia.mapping.validation.ConstraintViolation;
import com.github.jmkgreen.morphia.mapping.validation.ConstraintViolation.Level;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 *
 */
public class MultipleVersions implements ClassConstraint {
	
	public void check(MappedClass mc, Set<ConstraintViolation> ve) {
		List<MappedField> versionFields = mc.getFieldsAnnotatedWith(Version.class);
		if (versionFields.size() > 1)
			ve.add(new ConstraintViolation(Level.FATAL, mc, this.getClass(), "Multiple @" + Version.class
					+ " annotations are not allowed. (" + new FieldEnumString(versionFields)));
	}
}
