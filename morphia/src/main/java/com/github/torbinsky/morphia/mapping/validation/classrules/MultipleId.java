/**
 *
 */
package com.github.torbinsky.morphia.mapping.validation.classrules;

import com.github.torbinsky.morphia.annotations.Id;
import com.github.torbinsky.morphia.mapping.MappedClass;
import com.github.torbinsky.morphia.mapping.MappedField;
import com.github.torbinsky.morphia.mapping.validation.ClassConstraint;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolation;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolation.Level;
import java.util.List;
import java.util.Set;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class MultipleId implements ClassConstraint {

    public void check(MappedClass mc, Set<ConstraintViolation> ve) {

        List<MappedField> idFields = mc.getFieldsAnnotatedWith(Id.class);

        if (idFields.size() > 1) {
            ve.add(new ConstraintViolation(Level.FATAL, mc, this.getClass(), "More than one @" + Id.class.getSimpleName()
                    + " Field found (" + new FieldEnumString(idFields)
                    + ")."));
        }
    }

}
