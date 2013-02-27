/**
 *
 */
package com.github.torbinsky.morphia.mapping.lazy.proxy;

import com.github.torbinsky.morphia.Key;
import java.util.Map;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public interface ProxiedEntityReferenceMap extends ProxiedReference {

    void __put(Object key, Key<?> referenceKey);

    Map<Object, Key<?>> __getReferenceMap();
}
