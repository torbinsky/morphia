/**
 *
 */
package com.github.torbinsky.morphia.mapping.validation.fieldrules;

import com.github.torbinsky.morphia.mapping.MappedClass;
import com.github.torbinsky.morphia.mapping.MappedField;
import com.github.torbinsky.morphia.mapping.Mapper;
import com.github.torbinsky.morphia.mapping.validation.ClassConstraint;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolation;
import java.util.Set;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public abstract class FieldConstraint implements ClassConstraint {
    Mapper mapr;

    public final void check(MappedClass mc, Set<ConstraintViolation> ve) {
        for (MappedField mf : mc.getPersistenceFields()) {
            check(mc, mf, ve);
        }
    }

    protected abstract void check(MappedClass mc, MappedField mf, Set<ConstraintViolation> ve);

}
