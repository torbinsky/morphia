/**
 *
 */
package com.github.torbinsky.morphia.mapping.validation.classrules;

import com.github.torbinsky.morphia.annotations.Version;
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
public class MultipleVersions implements ClassConstraint {

    public void check(MappedClass mc, Set<ConstraintViolation> ve) {
        List<MappedField> versionFields = mc.getFieldsAnnotatedWith(Version.class);
        if (versionFields.size() > 1)
            ve.add(new ConstraintViolation(Level.FATAL, mc, this.getClass(), "Multiple @" + Version.class
                    + " annotations are not allowed. (" + new FieldEnumString(versionFields)));
    }
}
