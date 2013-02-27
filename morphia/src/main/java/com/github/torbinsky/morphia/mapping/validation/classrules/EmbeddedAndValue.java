/**
 *
 */
package com.github.torbinsky.morphia.mapping.validation.classrules;

import com.github.torbinsky.morphia.annotations.Embedded;
import com.github.torbinsky.morphia.mapping.MappedClass;
import com.github.torbinsky.morphia.mapping.Mapper;
import com.github.torbinsky.morphia.mapping.validation.ClassConstraint;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolation;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolation.Level;
import java.util.Set;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class EmbeddedAndValue implements ClassConstraint {

    public void check(MappedClass mc, Set<ConstraintViolation> ve) {

        if (mc.getEmbeddedAnnotation() != null && !mc.getEmbeddedAnnotation().value().equals(Mapper.IGNORED_FIELDNAME)) {
            ve.add(new ConstraintViolation(Level.FATAL, mc, this.getClass(), "@" + Embedded.class.getSimpleName()
                    + " classes cannot specify a fieldName value(); this is on applicable on fields"));
        }
    }

}
