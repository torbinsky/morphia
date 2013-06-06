/**
 * Copyright (C) 2010 Olafur Gauti Gudmundsson Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.github.torbinsky.morphia.mapping;

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Pattern;

import org.bson.BSONEncoder;
import org.bson.BasicBSONEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.torbinsky.morphia.EntityInterceptor;
import com.github.torbinsky.morphia.Key;
import com.github.torbinsky.morphia.annotations.Converters;
import com.github.torbinsky.morphia.annotations.Embedded;
import com.github.torbinsky.morphia.annotations.Entity;
import com.github.torbinsky.morphia.annotations.Id;
import com.github.torbinsky.morphia.annotations.NotSaved;
import com.github.torbinsky.morphia.annotations.PostLoad;
import com.github.torbinsky.morphia.annotations.PreLoad;
import com.github.torbinsky.morphia.annotations.PrePersist;
import com.github.torbinsky.morphia.annotations.PreSave;
import com.github.torbinsky.morphia.annotations.Property;
import com.github.torbinsky.morphia.annotations.Reference;
import com.github.torbinsky.morphia.annotations.Serialized;
import com.github.torbinsky.morphia.converters.DefaultConverters;
import com.github.torbinsky.morphia.converters.TypeConverter;
import com.github.torbinsky.morphia.mapping.cache.EntityCache;
import com.github.torbinsky.morphia.mapping.cache.EntityCacheFactory;
import com.github.torbinsky.morphia.mapping.cache.NoOpEntityCacheFactory;
import com.github.torbinsky.morphia.mapping.lazy.DatastoreProvider;
import com.github.torbinsky.morphia.mapping.lazy.LazyProxyFactory;
import com.github.torbinsky.morphia.mapping.lazy.proxy.ProxiedEntityReference;
import com.github.torbinsky.morphia.mapping.lazy.proxy.ProxyHelper;
import com.github.torbinsky.morphia.query.FilterOperator;
import com.github.torbinsky.morphia.query.ValidationException;
import com.github.torbinsky.morphia.utils.ReflectionUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

/**
 * <p>This is the heart of Morphia and takes care of mapping from/to POJOs/DBObjects<p>
 * <p>This class is thread-safe and keeps various "cached" data which should speed up processing.</p>
 *
 * @author Olafur Gauti Gudmundsson
 * @author Scott Hernandez
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class DefaultMapper implements Mapper {
	static Logger log = LoggerFactory.getLogger(DefaultMapper.class);
    protected EntityCacheFactory entityCacheFactory = new NoOpEntityCacheFactory();
   

    /**
     * Set of classes that registered by this mapper
     */
    protected final Map<String, MappedClass> mappedClasses = new ConcurrentHashMap<String, MappedClass>();
    protected final ConcurrentHashMap<String, Set<MappedClass>> mappedClassesByCollection = new ConcurrentHashMap<String, Set<MappedClass>>();

    //EntityInterceptors; these are called before EntityListeners and lifecycle methods on an Entity, for all Entities
    protected final List<EntityInterceptor> interceptors = new LinkedList<EntityInterceptor>();

    //A general cache of instances of classes; used by MappedClass for EntityListener(s)
    final Map<Class, Object> instanceCache = new ConcurrentHashMap();

    protected MapperOptions opts;

    private LazyProxyFactory proxyFactory;
    private DatastoreProvider datastoreProvider;
    DefaultConverters converters;

    public DefaultMapper() {
    	this(new MapperOptions());
    }

    public DefaultMapper(MapperOptions opts) {
        this.opts = opts;
        this.setProxyFactory(opts.proxyFactory);
        this.setDatastoreProvider(opts.datastoreProvider);
        this.converters = opts.converters;
    	converters.setMapper(this);
    }

    /**
     * Enable overriding of EntityCacheFactory.
     * @param factory
     * @return
     * @since 1.2.4
     */
    public Mapper setEntityCacheFactory(EntityCacheFactory factory) {
        this.entityCacheFactory = factory;
        return this;
    }

    /**
     * <p>
     * Adds an {@link EntityInterceptor}. Each instance is called
     * </p>
     */
    public void addInterceptor(final EntityInterceptor ei) {
        interceptors.add(ei);
    }

    /**
     * <p>
     * Gets list of {@link EntityInterceptor}s
     * </p>
     */
    public Collection<EntityInterceptor> getInterceptors() {
        return interceptors;
    }

    public MapperOptions getOptions() {
        return this.opts;
    }

    public void setOptions(MapperOptions options) {
        this.opts = options;
    }

    public boolean isMapped(final Class c) {
        return mappedClasses.containsKey(c.getName());
    }

    /**
     * Creates a MappedClass and validates it.
     */
    public MappedClass addMappedClass(Class c) {
        MappedClass mc = new MappedClass(c, this);
        return addMappedClass(mc, true);
    }

    /**
     * Validates MappedClass and adds to internal cache.
     */
    public MappedClass addMappedClass(MappedClass mc) {
        return addMappedClass(mc, true);
    }


    /**
     * Add MappedClass to internal cache, possibly validating first.
     */
    protected MappedClass addMappedClass(MappedClass mc, boolean validate) {
        if (validate)
            mc.validate();

        Converters c = (Converters) mc.getAnnotation(Converters.class);
        if (c != null)
            for (Class<? extends TypeConverter> clazz : c.value())
                if (!converters.isRegistered(clazz))
                    converters.addConverter(clazz);

        mappedClasses.put(mc.getClazz().getName(), mc);

        Set<MappedClass> mcs = mappedClassesByCollection.get(mc.getCollectionName());
        if (mcs == null) {
            mcs = new CopyOnWriteArraySet<MappedClass>();
            Set<MappedClass> temp = mappedClassesByCollection.putIfAbsent(mc.getCollectionName(), mcs);
            if (temp != null) mcs = temp;
        }

        mcs.add(mc);

        return mc;
    }

    /**
     * Returns collection of MappedClasses
     */
    public Collection<MappedClass> getMappedClasses() {
        return new ArrayList<MappedClass>(mappedClasses.values());
    }

    /**
     * Returns map of MappedClasses by class name
     */
    public Map<String, MappedClass> getMCMap() {
        return Collections.unmodifiableMap(mappedClasses);
    }

    /**
     * <p>
     * Gets the {@link MappedClass} for the object (type). If it isn't mapped,
     * create a new class and cache it (without validating).
     * </p>
     */
    public MappedClass getMappedClass(final Object obj) {
        if (obj == null)
            return null;

        Class type = (obj instanceof Class) ? (Class) obj : obj.getClass();
        if (ProxyHelper.isProxy(obj))
            type = ProxyHelper.getReferentClass(obj);

        MappedClass mc = mappedClasses.get(type.getName());
        if (mc == null) {
            mc = new MappedClass(type, this);
            // no validation
            addMappedClass(mc, false);
        }
        return mc;
    }

    public String getCollectionName(Object object) {
        if (object == null) throw new IllegalArgumentException();

        MappedClass mc = getMappedClass(object);
        return mc.getCollectionName();
    }

    /**
     * <p>
     * Updates the @{@link Id} fields.
     * </p>
     *
     * @param entity The object to update
     * @param dbObj  Value to update with; null means skip
     */
    public void updateKeyInfo(final Object entity, final DBObject dbObj, EntityCache cache) {
        MappedClass mc = getMappedClass(entity);

        // update id field, if there.
        if ((mc.getIdField() != null) && (dbObj != null) && (dbObj.get(ID_KEY) != null)) {
            try {
                MappedField mf = mc.getMappedIdField();
                Object oldIdValue = mc.getIdField().get(entity);
                readMappedField(dbObj, mf, entity, cache);
                Object dbIdValue = mc.getIdField().get(entity);
                if (oldIdValue != null) {
                    // The entity already had an id set. Check to make sure it
                    // hasn't changed. That would be unexpected, and could
                    // indicate a bad state.
                    if (!dbIdValue.equals(oldIdValue)) {
                        mf.setFieldValue(entity, oldIdValue);//put the value back...

                        throw new RuntimeException("@Id mismatch: " + oldIdValue + " != " + dbIdValue + " for "
                                + entity.getClass().getName());
                    }
                } else
                    mc.getIdField().set(entity, dbIdValue);
            } catch (Exception e) {
                if (e.getClass().equals(RuntimeException.class)) {
                    throw (RuntimeException) e;
                }

                throw new RuntimeException("Error setting @Id field after save/insert.", e);
            }
        }
    }

    /**
     * Converts a DBObject back to a type-safe java object (POJO)
     *
     * @param entityClass The type to return, or use; can be overridden by the @see Mapper.CLASS_NAME_FIELDNAME in the DBObject
     */
    public Object fromDBObject(final Class entityClass, final DBObject dbObject, EntityCache cache) {
        if (dbObject == null) {
            Throwable t = new Throwable();
            log.error("Somebody passed in a null dbObject; bad client!", t);
            return null;
        }

        Object entity = null;
        entity = opts.objectFactory.createInstance(entityClass, dbObject);
        entity = fromDBObject(dbObject, entity, cache);
        return entity;
    }
    
    /**
     * <p>
     * Converts a java object to a mongo-compatible object (possibly a DBObject
     * for complex mappings). Very similar to {@link DefaultMapper#toDBObject}
     * </p>
     * <p>
     * Used (mainly) by query/update operations
     * </p>
     */
    public Object toMongoObject(MappedField mf, MappedClass mc, Object value) {
        if (value == null) {
            return null;
        }

        Object mappedValue = value;

        //convert the value to Key (DBRef) if the field is @Reference or type is Key/DBRef, or if the destination class is an @Entity
        if (isAnAssignableField(mf, value) || isAnEntity(mc)) {
            try {
                if (value instanceof Iterable) {
                    return getDbRefs((Iterable) value);
                }

                if (value.getClass().isAssignableFrom(Boolean.class)) {
                    return toMongoObject(value, false);
                }
                if (value.getClass().isAssignableFrom(Integer.class)) {
                    return toMongoObject(value, false);
                }

                Key<?> k = (value instanceof Key) ? (Key<?>) value : getKey(value);
                mappedValue = keyToRef(k);
                if (mappedValue == value) {
                    throw new ValidationException("cannot map to @Reference/Key<T>/DBRef field: " + value);
                }
                return mappedValue;
            } catch (Exception e) {
                log.error("Error converting value(" + value + ") to reference.", e);
                return toMongoObject(value, false);
            }
        }

        //serialized
        if (mf != null && mf.hasAnnotation(Serialized.class)) {
            try {
                return Serializer.serialize(value, !mf.getAnnotation(Serialized.class).disableCompression());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        //pass-through
        if (value instanceof DBObject) {
            return value;
        }

        mappedValue = toMongoObject(value, EmbeddedMapper.shouldSaveClassName(value, mappedValue, mf));
        if (mappedValue instanceof DBObject && !EmbeddedMapper.shouldSaveClassName(value, mappedValue, mf)) {
            ((DBObject) mappedValue).removeField(CLASS_NAME_FIELDNAME);
        }

        return mappedValue;
    }

    /**
     * <p>
     * Converts a java object to a mongo-compatible object (possibly a DBObject
     * for complex mappings). Very similar to {@link DefaultMapper#toDBObject}
     * </p>
     * <p>
     * Used (mainly) by query/update operations
     * </p>
     */
    public Object toMongoObject(Object javaObj, boolean includeClassName) {
        if (javaObj == null)
            return null;

        Class origClass = javaObj.getClass();

        if (origClass.isAnonymousClass() && origClass.getSuperclass().isEnum())
            origClass = origClass.getSuperclass();

        Object newObj = converters.encode(origClass, javaObj);
        if (newObj == null) {
            log.warn("converted " + javaObj + " to null");
            return newObj;
        }
        Class type = newObj.getClass();
        boolean bSameType = origClass.equals(type);

        //TODO: think about this logic a bit more.
        //Even if the converter changed it, should it still be processed?
        if (!bSameType && !(Map.class.isAssignableFrom(type) || Iterable.class.isAssignableFrom(type)))
            return newObj;

        //The converter ran, and produced another type, or it is a list/map
        boolean isSingleValue = true;
        boolean isMap = false;
        Class subType = null;

        if (type.isArray() || Map.class.isAssignableFrom(type) || Iterable.class.isAssignableFrom(type)) {
            isSingleValue = false;
            isMap = ReflectionUtils.implementsInterface(type, Map.class);
            // subtype of Long[], List<Long> is Long
            subType = (type.isArray()) ? type.getComponentType() : ReflectionUtils.getParameterizedClass(type, (isMap) ? 1 : 0);
        }

        if (isSingleValue && !ReflectionUtils.isPropertyType(type)) {
            DBObject dbObj = toDBObject(newObj);

            if (!includeClassName)
                dbObj.removeField(CLASS_NAME_FIELDNAME);

            return dbObj;
        }
        if (newObj instanceof DBObject) {
            return newObj;
        }

        if (isMap) {
            if (ReflectionUtils.isPropertyType(subType))
                return toDBObject(newObj);

            HashMap m = new HashMap();
            for (Map.Entry e : (Iterable<Map.Entry>) ((Map) newObj).entrySet())
                m.put(e.getKey(), toMongoObject(e.getValue(), includeClassName));

            return m;
            //Set/List but needs elements converted
        }

        if (!isSingleValue && !ReflectionUtils.isPropertyType(subType)) {
            ArrayList<Object> vals = new ArrayList<Object>();
            if (type.isArray())
                for (Object obj : (Object[]) newObj)
                    vals.add(toMongoObject(obj, includeClassName));
            else
                for (Object obj : (Iterable) newObj)
                    vals.add(toMongoObject(obj, includeClassName));

            return vals;
        }

        return newObj;
    }
    
//
//    /**
//     * <p>
//     * Converts a java object to a mongo-compatible object (possibly a DBObject
//     * for complex mappings). Very similar to {@link DefaultMapper#toDBObject}
//     * </p>
//     * <p>
//     * Used (mainly) by query/update operations
//     * </p>
//     */
//<<<<<<< HEAD:morphia/src/main/java/com/github/torbinsky/morphia/mapping/DefaultMapper.java
//    Object toMongoObjectFromJavaObj(Object javaObj, boolean includeClassName) {
//=======
//    public Object toMongoObject(MappedField mf, MappedClass mc, Object value) {
//        if (value == null) {
//            return null;
//        }
//
//        Object mappedValue = value;
//
//        //convert the value to Key (DBRef) if the field is @Reference or type is Key/DBRef, or if the destination class is an @Entity
//        if (isAnAssignableField(mf, value) || isAnEntity(mc)) {
//            try {
//                if (value instanceof Iterable) {
//                    return getDbRefs((Iterable) value);
//                }
//
//                if (value.getClass().isAssignableFrom(Boolean.class)) {
//                    return toMongoObject(value, false);
//                }
//                if (value.getClass().isAssignableFrom(Integer.class)) {
//                    return toMongoObject(value, false);
//                }
//
//                Key<?> k = (value instanceof Key) ? (Key<?>) value : getKey(value);
//                mappedValue = keyToRef(k);
//                if (mappedValue == value) {
//                    throw new ValidationException("cannot map to @Reference/Key<T>/DBRef field: " + value);
//                }
//                return mappedValue;
//            } catch (Exception e) {
//                log.error("Error converting value(" + value + ") to reference.", e);
//                return toMongoObject(value, false);
//            }
//        }
//
//        //serialized
//        if (mf != null && mf.hasAnnotation(Serialized.class)) {
//            try {
//                return Serializer.serialize(value, !mf.getAnnotation(Serialized.class).disableCompression());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//        }
//
//        //pass-through
//        if (value instanceof DBObject) {
//            return value;
//        }
//
//        mappedValue = toMongoObject(value, EmbeddedMapper.shouldSaveClassName(value, mappedValue, mf));
//        if (mappedValue instanceof DBObject && !EmbeddedMapper.shouldSaveClassName(value, mappedValue, mf)) {
//            ((DBObject) mappedValue).removeField(CLASS_NAME_FIELDNAME);
//        }
//
//        return mappedValue;
//    }
//
//    /**
//     * <p>
//     * Converts a java object to a mongo-compatible object (possibly a DBObject
//     * for complex mappings). Very similar to {@link DefaultMapper#toDBObject}
//     * </p>
//     * <p>
//     * Used (mainly) by query/update operations
//     * </p>
//     */
//    public Object toMongoObject(Object javaObj, boolean includeClassName) {
//>>>>>>> 0842ce80c79a1b4a4e192525f1e936c038f0c876:morphia/src/main/java/com/github/jmkgreen/morphia/mapping/DefaultMapper.java
//        if (javaObj == null)
//            return null;
//
//        Class origClass = javaObj.getClass();
//
//        if (origClass.isAnonymousClass() && origClass.getSuperclass().isEnum())
//            origClass = origClass.getSuperclass();
//
//        Object newObj = converters.encode(origClass, javaObj);
//        if (newObj == null) {
//            log.warn("converted " + javaObj + " to null");
//            return newObj;
//        }
//        Class type = newObj.getClass();
//        boolean bSameType = origClass.equals(type);
//
//        //TODO: think about this logic a bit more.
//        //Even if the converter changed it, should it still be processed?
//        if (!bSameType && !(Map.class.isAssignableFrom(type) || Iterable.class.isAssignableFrom(type)))
//            return newObj;
//
//        //The converter ran, and produced another type, or it is a list/map
//        boolean isSingleValue = true;
//        boolean isMap = false;
//        Class subType = null;
//
//        if (type.isArray() || Map.class.isAssignableFrom(type) || Iterable.class.isAssignableFrom(type)) {
//            isSingleValue = false;
//            isMap = ReflectionUtils.implementsInterface(type, Map.class);
//            // subtype of Long[], List<Long> is Long
//            subType = (type.isArray()) ? type.getComponentType() : ReflectionUtils.getParameterizedClass(type, (isMap) ? 1 : 0);
//        }
//
//<<<<<<< HEAD:morphia/src/main/java/com/github/torbinsky/morphia/mapping/DefaultMapper.java
//            if (isSingleValue && !ReflectionUtils.isPropertyType(type)) {
//                DBObject dbObj = toDBObject(newObj);
//                if (!includeClassName)
//                    dbObj.removeField(CLASS_NAME_FIELDNAME);
//                return dbObj;
//            } else if (newObj instanceof DBObject) {
//                return newObj;
//            } else if (isMap) {
//                if (ReflectionUtils.isPropertyType(subType))
//                    return toDBObject(newObj);
//                else {
//                    HashMap m = new HashMap();
//                    for (Map.Entry e : (Iterable<Map.Entry>) ((Map) newObj).entrySet())
//                        m.put(e.getKey(), toMongoObjectFromJavaObj(e.getValue(), includeClassName));
//
//                    return m;
//                }
//                //Set/List but needs elements converted
//            } else if (!isSingleValue && !ReflectionUtils.isPropertyType(subType)) {
//                ArrayList<Object> vals = new ArrayList<Object>();
//                if (type.isArray())
//                    for (Object obj : (Object[]) newObj)
//                        vals.add(toMongoObjectFromJavaObj(obj, includeClassName));
//                else
//                    for (Object obj : (Iterable) newObj)
//                        vals.add(toMongoObjectFromJavaObj(obj, includeClassName));
//
//                return vals;
//            } else {
//                return newObj;
//            }
//=======
//        if (isSingleValue && !ReflectionUtils.isPropertyType(type)) {
//            DBObject dbObj = toDBObject(newObj);
//
//            if (!includeClassName)
//                dbObj.removeField(CLASS_NAME_FIELDNAME);
//
//            return dbObj;
//        }
//        if (newObj instanceof DBObject) {
//            return newObj;
//>>>>>>> 0842ce80c79a1b4a4e192525f1e936c038f0c876:morphia/src/main/java/com/github/jmkgreen/morphia/mapping/DefaultMapper.java
//        }
//
//        if (isMap) {
//            if (ReflectionUtils.isPropertyType(subType))
//                return toDBObject(newObj);
//
//<<<<<<< HEAD:morphia/src/main/java/com/github/torbinsky/morphia/mapping/DefaultMapper.java
//    /**
//     * <p>
//     * Converts a java object to a mongo-compatible object (possibly a DBObject
//     * for complex mappings). Very similar to {@link DefaultMapper#toDBObject}
//     * </p>
//     * <p>
//     * Used (mainly) by query/update operations
//     * </p>
//     */
//    public Object toMongoObject(MappedField mf, MappedClass mc, Object value, boolean ignoreRefs) {
//        Object mappedValue = value;
//
//        //convert the value to Key (DBRef) if the field is @Reference or type is Key/DBRef, or if the destination class is an @Entity
//        if (	!ignoreRefs && (
//        		(mf != null && (mf.hasAnnotation(Reference.class) ||
//                mf.getType().isAssignableFrom(Key.class) ||
//                mf.getType().isAssignableFrom(DBRef.class) ||
//                //Collection/Array/???
//                (value instanceof Iterable && mf.isMultipleValues() && (
//                        mf.getSubClass().isAssignableFrom(Key.class) ||
//                                mf.getSubClass().isAssignableFrom(DBRef.class))
//                )
//        )) || (mc != null && mc.getEntityAnnotation() != null))) {
//            try {
//                if (value instanceof Iterable) {
//                    ArrayList<DBRef> refs = new ArrayList<DBRef>();
//                    Iterable it = (Iterable) value;
//                    for (Object o : it) {
//                        Key<?> k = (o instanceof Key) ? (Key<?>) o : getKey(o);
//                        DBRef dbref = keyToRef(k);
//                        refs.add(dbref);
//                    }
//                    mappedValue = refs;
//                } else {
//                    if (value == null)
//                        mappedValue = null;
//
//                    Key<?> k = (value instanceof Key) ? (Key<?>) value : getKey(value);
//                    mappedValue = keyToRef(k);
//                    if (mappedValue == value)
//                        throw new ValidationException("cannot map to @Reference/Key<T>/DBRef field: " + value);
//                }
//            } catch (Exception e) {
//                log.warn("Error converting value(" + value + ") to reference.", e);
//                mappedValue = toMongoObjectFromJavaObj(value, false);
//            }
//        }//serialized
//        else if (mf != null && mf.hasAnnotation(Serialized.class))
//            try {
//                mappedValue = Serializer.serialize(value, !mf.getAnnotation(Serialized.class).disableCompression());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            //pass-through
//        else if (value instanceof DBObject)
//            mappedValue = value;
//        else {
//            mappedValue = toMongoObjectFromJavaObj(value, EmbeddedMapper.shouldSaveClassName(value, mappedValue, mf));
//            if (mappedValue instanceof DBObject && !EmbeddedMapper.shouldSaveClassName(value, mappedValue, mf))
//                ((DBObject) mappedValue).removeField(CLASS_NAME_FIELDNAME);
//        }
//=======
//            HashMap m = new HashMap();
//            for (Map.Entry e : (Iterable<Map.Entry>) ((Map) newObj).entrySet())
//                m.put(e.getKey(), toMongoObject(e.getValue(), includeClassName));
//
//            return m;
//            //Set/List but needs elements converted
//        }
//
//        if (!isSingleValue && !ReflectionUtils.isPropertyType(subType)) {
//            ArrayList<Object> vals = new ArrayList<Object>();
//            if (type.isArray())
//                for (Object obj : (Object[]) newObj)
//                    vals.add(toMongoObject(obj, includeClassName));
//            else
//                for (Object obj : (Iterable) newObj)
//                    vals.add(toMongoObject(obj, includeClassName));
//
//            return vals;
//        }
//
//        return newObj;
//    }
//
//    private boolean isAnAssignableField(MappedField mf, Object value) {
//        if (mf == null) {
//            return false;
//        }
//
//        return (mf.hasAnnotation(Reference.class) ||
//                mf.getType().isAssignableFrom(Key.class) ||
//                mf.getType().isAssignableFrom(DBRef.class) ||
//                //Collection/Array/???
//                isValueIterableAndTargetCompatible(mf, value)
//        );
//    }
//>>>>>>> 0842ce80c79a1b4a4e192525f1e936c038f0c876:morphia/src/main/java/com/github/jmkgreen/morphia/mapping/DefaultMapper.java

    private boolean isAnAssignableField(MappedField mf, Object value) {
        if (mf == null) {
            return false;
        }

        return (mf.hasAnnotation(Reference.class) ||
                mf.getType().isAssignableFrom(Key.class) ||
                mf.getType().isAssignableFrom(DBRef.class) ||
                //Collection/Array/???
                isValueIterableAndTargetCompatible(mf, value)
        );
    }
    
    private boolean isValueIterableAndTargetCompatible(MappedField mf, Object value) {
        return (value instanceof Iterable && mf.isMultipleValues() && (
                mf.getSubClass().isAssignableFrom(Key.class) ||
                        mf.getSubClass().isAssignableFrom(DBRef.class))
        );
    }

    private boolean isAnEntity(MappedClass mc) {
        return (mc != null && mc.getEntityAnnotation() != null);
    }

    private ArrayList<DBRef> getDbRefs(Iterable value) {
        ArrayList<DBRef> refs = new ArrayList<DBRef>();
        for (Object o : value) {
            Key<?> k = (o instanceof Key) ? (Key<?>) o : getKey(o);
            refs.add(keyToRef(k));
        }
        return refs;
    }

    public Object getId(Object entity) {
        if (entity == null) return null;
        entity = ProxyHelper.unwrap(entity);
//		String keyClassName = entity.getClass().getName();
        MappedClass mc = getMappedClass(entity.getClass());
//
//		if (getMappedClasses().containsKey(keyClassName))
//			mc = getMappedClasses().get(keyClassName);
//		else
//			mc = new MappedClass(entity.getClass(), getMapper());
        try {
            return mc.getIdField().get(entity);
        } catch (Exception e) {
            return null;
        }
    }

    public <T> Key<T> getKey(T entity) {
        if (entity == null)
            throw new MappingException("getKey has been passed a null entity");

        if (entity instanceof ProxiedEntityReference) {
            return getKey((ProxiedEntityReference) entity);
        }

        entity = ProxyHelper.unwrap(entity);
        if (entity instanceof Key)
            return (Key<T>) entity;

        Object id = getId(entity);
        if (id == null)
            throw new MappingException("Could not get id for " + entity.getClass().getName());

        return new Key<T>((Class<T>) entity.getClass(), id);
    }

    private <T> Key<T> getKey(ProxiedEntityReference entity) {
        return (Key<T>) entity.__getKey();
    }

    /**
     * <p>
     * Converts an entity (POJO) to a DBObject; A special field will be added to keep track of the class: {@link Mapper#CLASS_NAME_FIELDNAME}
     * </p>
     *
     * @param entity The POJO
     */
    public DBObject toDBObject(Object entity) {
        return toDBObject(entity, null);
    }

    /**
     * <p> Converts an entity (POJO) to a DBObject (for use with low-level driver); A special field will be added to keep track of the class: {@link DefaultMapper#CLASS_NAME_FIELDNAME} </p>
     *
     * @param entity          The POJO
     * @param involvedObjects A Map of (already converted) POJOs
     */
    public DBObject toDBObject(Object entity, Map<Object, DBObject> involvedObjects) {
        return toDBObject(entity, involvedObjects, true);
    }

    DBObject toDBObject(Object entity, Map<Object, DBObject> involvedObjects, boolean lifecycle) {

        DBObject dbObject = new BasicDBObject();
        MappedClass mc = getMappedClass(entity);

        if (mc.getEntityAnnotation() == null || mc.isClassNameStored())
            dbObject.put(CLASS_NAME_FIELDNAME, entity.getClass().getName());

        if (lifecycle)
            dbObject = (DBObject) mc.callLifecycleMethods(PrePersist.class, entity, dbObject, this);

        for (MappedField mf : mc.getMappedFields()) {
            try {
                writeMappedField(dbObject, mf, entity, involvedObjects);
            } catch (Exception e) {
                throw new MappingException("Error mapping field:" + mf.getFullName(), e);
            }
        }
        if (involvedObjects != null)
            involvedObjects.put(entity, dbObject);

        if (lifecycle)
            mc.callLifecycleMethods(PreSave.class, entity, dbObject, this);

        return dbObject;
    }

    public Object fromDBObject(DBObject dbObject, Object entity, EntityCache cache) {
        //hack to bypass things and just read the value.
        if (entity instanceof MappedField) {
            readMappedField(dbObject, (MappedField) entity, entity, cache);
            return entity;
        }

        // check the history key (a key is the namespace + id)

        if (dbObject.containsField(ID_KEY) && getMappedClass(entity).getIdField() != null
                && getMappedClass(entity).getEntityAnnotation() != null) {
            Key key = new Key(entity.getClass(), dbObject.get(ID_KEY));
            Object cachedInstance = cache.getEntity(key);
            if (cachedInstance != null)
                return cachedInstance;
            else
                cache.putEntity(key, entity); // to avoid stackOverflow in recursive refs
        }

        MappedClass mc = getMappedClass(entity);

        dbObject = (DBObject) mc.callLifecycleMethods(PreLoad.class, entity, dbObject, this);
        try {
            for (MappedField mf : mc.getMappedFields()) {
                readMappedField(dbObject, mf, entity, cache);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (dbObject.containsField(ID_KEY) && getMappedClass(entity).getIdField() != null) {
            Key key = new Key(entity.getClass(), dbObject.get(ID_KEY));
            cache.putEntity(key, entity);
        }
        mc.callLifecycleMethods(PostLoad.class, entity, dbObject, this);
        return entity;
    }

    protected void readMappedField(DBObject dbObject, MappedField mf, Object entity, EntityCache cache) {
        if (mf.hasAnnotation(Property.class) || mf.hasAnnotation(Serialized.class)
                || mf.isTypeMongoCompatible() || converters.hasSimpleValueConverter(mf))
            opts.valueMapper.fromDBObject(dbObject, mf, entity, cache, this);
        else if (mf.hasAnnotation(Embedded.class))
            opts.embeddedMapper.fromDBObject(dbObject, mf, entity, cache, this);
        else if (mf.hasAnnotation(Reference.class))
            opts.referenceMapper.fromDBObject(dbObject, mf, entity, cache, this);
        else {
            opts.defaultMapper.fromDBObject(dbObject, mf, entity, cache, this);
        }
    }

    protected void writeMappedField(DBObject dbObject, MappedField mf, Object entity, Map<Object, DBObject> involvedObjects) {
        Class<? extends Annotation> annType = null;

        //skip not saved fields.
        if (mf.hasAnnotation(NotSaved.class))
            return;

        // get the annotation from the field.
        for (Class<? extends Annotation> testType : new Class[]{Property.class,
                Embedded.class,
                Serialized.class,
                Reference.class}) {
            if (mf.hasAnnotation(testType)) {
                annType = testType;
                break;
            }
        }

        if (Property.class.equals(annType) || Serialized.class.equals(annType) || mf.isTypeMongoCompatible() ||
                (converters.hasSimpleValueConverter(mf) || (converters.hasSimpleValueConverter(mf.getFieldValue(entity)))))
            opts.valueMapper.toDBObject(entity, mf, dbObject, involvedObjects, this);
        else if (Reference.class.equals(annType))
            opts.referenceMapper.toDBObject(entity, mf, dbObject, involvedObjects, this);
        else if (Embedded.class.equals(annType)) {
            opts.embeddedMapper.toDBObject(entity, mf, dbObject, involvedObjects, this);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("No annotation was found, using default mapper {} for {}", opts.defaultMapper, mf);
            }
            opts.defaultMapper.toDBObject(entity, mf, dbObject, involvedObjects, this);
        }

    }

    // TODO might be better to expose via some "options" object?
    public DefaultConverters getConverters() {
        return converters;
    }

    public EntityCache createEntityCache() {
        return entityCacheFactory.create();
    }

    public <T> Key<T> refToKey(DBRef ref) {
        if (ref == null) return null;
        return new Key<T>(ref.getRef(), ref.getId());
    }

    public DBRef keyToRef(Key key) {
        if (key == null) return null;
        if (key.getKindClass() == null && key.getKind() == null)
            throw new IllegalStateException("How can it be missing both?");
        if (key.getKind() == null)
            key.setKind(getCollectionName(key.getKindClass()));

        return new DBRef(null, key.getKind(), toMongoObject(key.getId(), false));
    }

    public String updateKind(Key key) {
        if (key.getKind() == null && key.getKindClass() == null)
            throw new IllegalStateException("Key is invalid! " + toString());
        else if (key.getKind() == null)
            key.setKind(getMappedClass(key.getKindClass()).getCollectionName());

        return key.getKind();
    }

    public <T> Key<T> createKey(Class<T> clazz, Serializable id) {
        return new Key<T>(clazz, id);
    }

    public <T> Key<T> createKey(Class<T> clazz, Object id) {
        if (id instanceof Serializable)
            return createKey(clazz, (Serializable) id);

        //TODO: cache the encoders, maybe use the pool version of the buffer that the driver does.
        BSONEncoder enc = new BasicBSONEncoder();
        return new Key<T>(clazz, enc.encode(toDBObject(id)));
    }

    /**
     * Validate the path, and value type, returning the {@link MappedField} for the field at the path
     */
    public static MappedField validate(Class clazz, Mapper mapr, StringBuffer origProp, FilterOperator op, Object val, boolean validateNames, boolean validateTypes) {
        //TODO: cache validations (in static?).

        MappedField mf = null;
        String prop = origProp.toString();
        boolean hasTranslations = false;

        if (validateNames) {
            String[] parts = prop.split("\\.");
            if (clazz == null) return null;

            MappedClass mc = mapr.getMappedClass(clazz);
            for (int i = 0; ; ) {
                String part = parts[i];
                mf = mc.getMappedField(part);

                //translate from java field name to stored field name
                if (mf == null) {
                    mf = mc.getMappedFieldByJavaField(part);
                    if (mf == null)
                        throw new ValidationException("The field '" + part + "' could not be found in '" + clazz.getName() +
                                "' while validating - " + prop +
                                "; if you wish to continue please disable validation.");
                    hasTranslations = true;
                    parts[i] = mf.getNameToStore();
                }

                i++;
                if (mf.isMap()) {
                    //skip the map key validation, and move to the next part
                    i++;
                }

                //catch people trying to search/update into @Reference/@Serialized fields
                if (i < parts.length && !canQueryPast(mf))
                    throw new ValidationException("Can not use dot-notation past '" + part + "' could not be found in '" + clazz.getName() + "' while validating - " + prop);

                if (i >= parts.length) break;
                //get the next MappedClass for the next field validation
                mc = mapr.getMappedClass((mf.isSingleValue()) ? mf.getType() : mf.getSubClass());
            }

            //record new property string if there has been a translation to any part
            if (hasTranslations) {
                origProp.setLength(0); // clear existing content
                origProp.append(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    origProp.append('.');
                    origProp.append(parts[i]);
                }
            }

            if (validateTypes)
                if ((mf.isSingleValue() && !isCompatibleForOperator(mf.getType(), op, val)) ||
                        ((mf.isMultipleValues() && !(isCompatibleForOperator(mf.getSubClass(), op, val) || isCompatibleForOperator(mf.getType(), op, val))))) {


                    if (log.isDebugEnabled()) {
                        Throwable t = new Throwable();
                        StackTraceElement ste = getFirstClientLine(t);
                        log.debug("The type(s) for the query/update may be inconsistent; using an instance of type '"
                                + val.getClass().getName() + "' for the field '" + mf.getDeclaringClass().getName() + "." + mf.getJavaFieldName()
                                + "' which is declared as '" + mf.getType().getName() + (ste == null ? "'" : "'\r\n --@--" + ste));

                        if (log.isTraceEnabled())
                            log.trace("Location of warn:\r\n", t);
                    }
                }
        }
        return mf;
    }

    /**
     * Return the first {@link StackTraceElement} not in our code (package).
     */
    public static StackTraceElement getFirstClientLine(Throwable t) {
        for (StackTraceElement ste : t.getStackTrace())
            if (!ste.getClassName().startsWith("com.github.torbinsky.morphia") &&
                    !ste.getClassName().startsWith("sun.reflect") &&
                    !ste.getClassName().startsWith("org.junit") &&
                    !ste.getClassName().startsWith("org.eclipse") &&
                    !ste.getClassName().startsWith("java.lang"))
                return ste;

        return null;
    }

    /**
     * Returns if the MappedField is a Reference or Serialized
     */
    public static boolean canQueryPast(MappedField mf) {
        return !(mf.hasAnnotation(Reference.class) || mf.hasAnnotation(Serialized.class));
    }

    public static boolean isCompatibleForOperator(Class<?> type, FilterOperator op, Object value) {
        if (value == null || type == null)
            return true;
        else if (op.equals(FilterOperator.EXISTS) && (value instanceof Boolean))
            return true;
        else if (op.equals(FilterOperator.IN) && (value.getClass().isArray() || Iterable.class.isAssignableFrom(value.getClass()) || Map.class.isAssignableFrom(value.getClass())))
            return true;
        else if (op.equals(FilterOperator.NOT_IN) && (value.getClass().isArray() || Iterable.class.isAssignableFrom(value.getClass()) || Map.class.isAssignableFrom(value.getClass())))
            return true;
        else if (op.equals(FilterOperator.ALL) && (value.getClass().isArray() || Iterable.class.isAssignableFrom(value.getClass()) || Map.class.isAssignableFrom(value.getClass())))
            return true;
        else if (value instanceof Integer && (int.class.equals(type) || long.class.equals(type) || Long.class.equals(type)))
            return true;
        else if ((value instanceof Integer || value instanceof Long) && (double.class.equals(type) || Double.class.equals(type)))
            return true;
        else if (value instanceof Pattern && String.class.equals(type))
            return true;
        else if (value.getClass().getAnnotation(Entity.class) != null && Key.class.equals(type))
            return true;
        else if (value instanceof List<?>)
            return true;
        else if (!value.getClass().isAssignableFrom(type) &&
                //hack to let Long match long, and so on
                !value.getClass().getSimpleName().toLowerCase().equals(type.getSimpleName().toLowerCase())) {
            return false;
        }
        return true;
    }

    public Class<?> getClassFromKind(String kind) {
        Set<MappedClass> mcs = mappedClassesByCollection.get(kind);
        if (mcs.isEmpty())
            throw new MappingException("The collection '" + kind + "' is not mapped to a java class.");
        if (mcs.size() > 1)
            if (log.isInfoEnabled())
                log.info("Found more than one class mapped to collection '" + kind + "'" + mcs);
        return mcs.iterator().next().getClazz();
    }
    public boolean isCached(Class<?> clazz) {
    	return instanceCache.containsKey(clazz);
    }
    public void cacheClass(Class<?> clazz, Object instance) throws ConcurrentModificationException {
    	Object previousKey = instanceCache.put(clazz, instance);
    	if (previousKey != null) {
    		throw new ConcurrentModificationException("Duplicate class created "+clazz);
    	}
    }
    public Object getCachedClass(Class<?> clazz) {
    	return instanceCache.get(clazz);
    }

    public LazyProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public void setProxyFactory(LazyProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    public DatastoreProvider getDatastoreProvider() {
        return datastoreProvider;
    }

    public void setDatastoreProvider(DatastoreProvider datastoreProvider) {
        this.datastoreProvider = datastoreProvider;
    }
}
