/**
 *
 */
package com.github.torbinsky.morphia.logging.slf4j;

import com.github.torbinsky.morphia.logging.Logr;
import com.github.torbinsky.morphia.logging.LogrFactory;

/**
 * @author doc
 */
public class SLF4JLogrImplFactory implements LogrFactory {
    /**
     *
     * @param c
     * @return
     */
    public Logr get(final Class<?> c) {
        return new SLF4JLogr(c);
    }

}
