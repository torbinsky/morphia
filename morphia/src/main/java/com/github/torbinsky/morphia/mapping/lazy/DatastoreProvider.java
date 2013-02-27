/**
 *
 */
package com.github.torbinsky.morphia.mapping.lazy;

import com.github.torbinsky.morphia.Datastore;
import java.io.Serializable;

/**
 * Lightweight object to be created (hopefully by a factoy some day) to create
 * provide a Datastore-reference to a resolving Proxy. If this was created by a
 * common Object factory, it could make use of the current context (like Guice
 * Scopes etc.)
 *
 * @author uwe schaefer
 * @see LazyProxyFactory
 */
public interface DatastoreProvider extends Serializable {
    Datastore get();
}
