package com.github.torbinsky.morphia.mapping.validation;

import com.github.torbinsky.morphia.mapping.MappedClass;
import java.util.Set;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public interface ClassConstraint {
    void check(MappedClass mc, Set<ConstraintViolation> ve);
}
