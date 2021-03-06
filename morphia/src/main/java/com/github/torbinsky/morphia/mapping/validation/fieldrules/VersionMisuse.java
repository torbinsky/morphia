/**
 *
 */
package com.github.torbinsky.morphia.mapping.validation.fieldrules;

import com.github.torbinsky.morphia.annotations.Version;
import com.github.torbinsky.morphia.mapping.DefaultCreator;
import com.github.torbinsky.morphia.mapping.MappedClass;
import com.github.torbinsky.morphia.mapping.MappedField;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolation;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolation.Level;
import java.util.Set;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class VersionMisuse extends FieldConstraint {

    @Override
    protected void check(MappedClass mc, MappedField mf, Set<ConstraintViolation> ve) {
        if (mf.hasAnnotation(Version.class)) {
            Class<?> type = mf.getType();
            if (Long.class.equals(type) || long.class.equals(type)) {

                //TODO: Replace this will a read ObjectFactory call -- requires Mapper instance.
                Object testInstance = DefaultCreator.createInst(mc.getClazz());

                // check initial value
                if (Long.class.equals(type)) {
                    if (mf.getFieldValue(testInstance) != null)
                        ve.add(new ConstraintViolation(Level.FATAL, mc, mf, this.getClass(), "When using @" + Version.class.getSimpleName()
                                + " on a Long field, it must be initialized to null."));
                } else if (long.class.equals(type)) {
                    if ((Long) mf.getFieldValue(testInstance) != 0L)
                        ve.add(new ConstraintViolation(Level.FATAL, mc, mf, this.getClass(), "When using @" + Version.class.getSimpleName()
                                + " on a long field, it must be initialized to 0."));
                }
            } else
                ve.add(new ConstraintViolation(Level.FATAL, mc, mf, this.getClass(), "@" + Version.class.getSimpleName() + " can only be used on a Long/long field."));
        }
    }

}
