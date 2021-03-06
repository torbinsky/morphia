/**
 *
 */
package com.github.torbinsky.morphia.mapping.validation.fieldrules;

import com.github.torbinsky.morphia.mapping.MappedClass;
import com.github.torbinsky.morphia.mapping.MappedField;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolation;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolation.Level;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class ContradictingFieldAnnotation extends FieldConstraint {

    private Class<? extends Annotation> a1;
    private Class<? extends Annotation> a2;

    public ContradictingFieldAnnotation(Class<? extends Annotation> a1, Class<? extends Annotation> a2) {
        this.a1 = a1;
        this.a2 = a2;
    }

    @Override
    protected final void check(MappedClass mc, MappedField mf, Set<ConstraintViolation> ve) {
        if (mf.hasAnnotation(a1) && mf.hasAnnotation(a2))
            ve.add(new ConstraintViolation(Level.FATAL, mc, mf, this.getClass(), "A field can be either annotated with @"
                    + a1.getSimpleName() + " OR @" + a2.getSimpleName() + ", but not both."));
    }
}
