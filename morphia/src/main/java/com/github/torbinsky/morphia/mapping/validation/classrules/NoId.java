/**
 *
 */
package com.github.torbinsky.morphia.mapping.validation.classrules;

import com.github.torbinsky.morphia.mapping.MappedClass;
import com.github.torbinsky.morphia.mapping.validation.ClassConstraint;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolation;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolation.Level;
import java.util.Set;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class NoId implements ClassConstraint {

    public void check(MappedClass mc, Set<ConstraintViolation> ve) {
        if (mc.getIdField() == null && mc.getEmbeddedAnnotation() == null) {
            ve.add(new ConstraintViolation(Level.FATAL, mc, this.getClass(),
                    "No field is annotated with @Id; but it is required"));
        }
    }

}
