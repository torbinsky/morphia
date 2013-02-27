package com.github.torbinsky.morphia.logging.jdk;

import com.github.torbinsky.morphia.logging.Logr;
import com.github.torbinsky.morphia.logging.LogrFactory;

public class JDKLoggerFactory implements LogrFactory {

    public Logr get(Class<?> c) {
        return new JDKLogger(c);
    }

}
