/**
 *
 */
package com.github.torbinsky.morphia.mapping.lazy.proxy;

import com.github.torbinsky.morphia.Key;

/**
 * @author Uwe Schaefer, (schaefer@thomas-daily.de)
 */
public interface ProxiedEntityReference extends ProxiedReference {
    Key<?> __getKey();
}
