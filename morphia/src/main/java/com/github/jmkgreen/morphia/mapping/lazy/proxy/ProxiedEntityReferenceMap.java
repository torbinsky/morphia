/**
 * 
 */
package com.github.jmkgreen.morphia.mapping.lazy.proxy;

import java.util.Map;

import com.github.jmkgreen.morphia.Key;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * 
 */
public interface ProxiedEntityReferenceMap extends ProxiedReference {

	void __put(String key, Key<?> referenceKey);
	
	Map<String, Key<?>> __getReferenceMap();
}
