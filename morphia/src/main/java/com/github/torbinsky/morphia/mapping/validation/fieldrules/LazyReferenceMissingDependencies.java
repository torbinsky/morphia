/**
 *
 */
package com.github.torbinsky.morphia.mapping.validation.fieldrules;

import com.github.torbinsky.morphia.annotations.Reference;
import com.github.torbinsky.morphia.mapping.MappedClass;
import com.github.torbinsky.morphia.mapping.MappedField;
import com.github.torbinsky.morphia.mapping.lazy.LazyFeatureDependencies;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolation;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolation.Level;
import java.util.Set;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class LazyReferenceMissingDependencies extends FieldConstraint {

    @Override
    protected void check(MappedClass mc, MappedField mf, Set<ConstraintViolation> ve) {
        Reference ref = mf.getAnnotation(Reference.class);
        if (ref != null) {
            if (ref.lazy()) {
                if (!LazyFeatureDependencies.testDependencyFullFilled())
                    ve.add(new ConstraintViolation(Level.SEVERE, mc, mf, this.getClass(),
                            "Lazy references need CGLib and Proxytoys in the classpath."));
            }
        }
    }

}
