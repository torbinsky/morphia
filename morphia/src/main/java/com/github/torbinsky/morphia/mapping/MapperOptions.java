package com.github.torbinsky.morphia.mapping;

import com.github.torbinsky.morphia.ObjectFactory;
import com.github.torbinsky.morphia.converters.DefaultConverters;
import com.github.torbinsky.morphia.mapping.lazy.CGLibLazyProxyFactory;
import com.github.torbinsky.morphia.mapping.lazy.DatastoreProvider;
import com.github.torbinsky.morphia.mapping.lazy.DefaultDatastoreProvider;
import com.github.torbinsky.morphia.mapping.lazy.LazyProxyFactory;

/**
 * Options to control mapping behavior.
 *
 * @author Scott Hernandez
 */
public class MapperOptions {
    /**
     * <p>Treat java transient fields as if they have <code>@Transient</code> on them</p>
     */
    public boolean actLikeSerializer = false;
    /**
     * <p>Controls if null are stored. </p>
     */
    public boolean storeNulls = false;
    /**
     * <p>Controls if empty collection/arrays are stored. </p>
     */
    public boolean storeEmpties = false;
    /**
     * <p>Controls if final fields are stored. </p>
     */
    public boolean ignoreFinals = false; //ignore final fields.

    public CustomMapper referenceMapper = new ReferenceMapper();
    public CustomMapper embeddedMapper = new EmbeddedMapper();
    public CustomMapper valueMapper = new ValueMapper();
    public CustomMapper defaultMapper = embeddedMapper;

    public ObjectFactory objectFactory = new DefaultCreator();
    public LazyProxyFactory proxyFactory = new CGLibLazyProxyFactory();
    public DatastoreProvider datastoreProvider = new DefaultDatastoreProvider();
    public DefaultConverters converters = new DefaultConverters();

}
