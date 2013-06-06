package com.github.torbinsky.morphia.mapping.cache;

/**
 * Default factory for entity cache.
 */
public class DefaultEntityCacheFactory implements EntityCacheFactory {

    /**
     * Returns a new {@link DefaultEntityCache}.
     * @return
     */
    public EntityCache create() {
        return new DefaultEntityCache();
    }
}
