/**
 *
 */
package com.github.torbinsky.morphia.mapping.lazy.proxy;

/**
 * @author Uwe Schäfer, (schaefer@thomas-daily.de)
 */
@SuppressWarnings("unchecked")
public interface ProxiedReference {
    boolean __isFetched();

    Class __getReferenceObjClass();

    Object __unwrap();
}
