/**
 *
 */
package com.github.torbinsky.morphia.ext.guice;

import com.github.torbinsky.morphia.Morphia;
import com.github.torbinsky.morphia.mapping.MapperOptions;
import com.github.torbinsky.morphia.utils.Assert;
import com.google.inject.Injector;

/**
 * @author us@thomas-daily.de
 */
public class GuiceExtension {

    /**
     *
     * @param morphia
     * @param injector
     */
    public GuiceExtension(final Morphia morphia, final Injector injector) {
        Assert.parameterNotNull(morphia, "morphia");
        final MapperOptions options = morphia.getMapper().getOptions();
        options.objectFactory =
                new GuiceObjectFactory(options.objectFactory, injector);
    }
}
