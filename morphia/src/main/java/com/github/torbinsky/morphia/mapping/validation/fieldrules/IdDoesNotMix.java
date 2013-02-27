/**
 *
 */
package com.github.torbinsky.morphia.mapping.validation.fieldrules;

import com.github.torbinsky.morphia.annotations.Embedded;
import com.github.torbinsky.morphia.annotations.Id;
import com.github.torbinsky.morphia.annotations.Property;
import com.github.torbinsky.morphia.annotations.Reference;
import com.github.torbinsky.morphia.mapping.MappedClass;
import com.github.torbinsky.morphia.mapping.MappedField;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolation;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolation.Level;
import java.util.Set;

/**
 * @author ScottHenandez
 */
public class IdDoesNotMix extends FieldConstraint {

    @Override
    protected void check(MappedClass mc, MappedField mf, Set<ConstraintViolation> ve) {
        // an @Id field can not be a Value, Reference, or Embedded
        if (mf.hasAnnotation(Id.class))
            if (mf.hasAnnotation(Reference.class) || mf.hasAnnotation(Embedded.class) || mf.hasAnnotation(Property.class))
                ve.add(new ConstraintViolation(Level.FATAL, mc, mf, this.getClass(), mf.getFullName() + " is annotated as @"
                        + Id.class.getSimpleName() + " and cannot be mixed with other annotations (like @Reference)"));
    }
}
