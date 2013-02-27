/**
 *
 */
package com.github.torbinsky.morphia.mapping.validation.classrules;

import com.github.torbinsky.morphia.annotations.Embedded;
import com.github.torbinsky.morphia.annotations.Entity;
import com.github.torbinsky.morphia.mapping.MappedClass;
import com.github.torbinsky.morphia.mapping.validation.ClassConstraint;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolation;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolation.Level;
import java.util.Set;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class EntityAndEmbed implements ClassConstraint {

    public void check(MappedClass mc, Set<ConstraintViolation> ve) {

        if (mc.getEntityAnnotation() != null && mc.getEmbeddedAnnotation() != null) {
            new ConstraintViolation(Level.FATAL, mc, this.getClass(), "Cannot have both @" + Entity.class.getSimpleName() + " and @"
                    + Embedded.class.getSimpleName() + " annotation at class level.");
        }

    }
}
