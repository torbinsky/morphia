package com.github.jmkgreen.morphia.mapping.validation;

import java.util.Set;

import com.github.jmkgreen.morphia.mapping.MappedClass;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public interface ClassConstraint {
	void check(MappedClass mc, Set<ConstraintViolation> ve);
}
