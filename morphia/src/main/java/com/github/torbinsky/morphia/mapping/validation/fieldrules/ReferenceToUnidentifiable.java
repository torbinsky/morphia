/**
 *
 */
package com.github.torbinsky.morphia.mapping.validation.fieldrules;

import com.github.torbinsky.morphia.annotations.Id;
import com.github.torbinsky.morphia.annotations.Reference;
import com.github.torbinsky.morphia.mapping.MappedClass;
import com.github.torbinsky.morphia.mapping.MappedField;
import com.github.torbinsky.morphia.mapping.MappingException;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolation;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolation.Level;
import java.util.Set;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class ReferenceToUnidentifiable extends FieldConstraint {

    @SuppressWarnings("unchecked")
    @Override
    protected void check(MappedClass mc, MappedField mf, Set<ConstraintViolation> ve) {
        if (mf.hasAnnotation(Reference.class)) {
            Class realType = (mf.isSingleValue()) ? mf.getType() : mf.getSubClass();

            if (realType == null) throw new MappingException("Type is null for this MappedField: " + mf);

            if ((!realType.isInterface() && mc.getMapper().getMappedClass(realType).getIdField() == null))
                ve.add(new ConstraintViolation(Level.FATAL, mc, mf, this.getClass(), mf.getFullName() + " is annotated as a @"
                        + Reference.class.getSimpleName() + " but the " + mf.getType().getName()
                        + " class is missing the @" + Id.class.getSimpleName() + " annotation"));
        }
    }

}
