package com.github.torbinsky.morphia;

import com.github.torbinsky.morphia.query.Query;
import com.github.torbinsky.morphia.query.UpdateOperations;
import com.mongodb.*;

/**
 * <p>
 * This interface exposes advanced {@link Datastore} features,
 * like interacting with DBObject and low-level options.
 * <p/>
 * <ul>
 * <li>Implements matching methods from the {@code Datastore} interface
 * but with a specified kind (collection name), or raw types (DBObject).
 * </li>
 * </ul>
 * </p>
 *
 * @author ScottHernandez
 */
public interface AdvancedDatastore extends Datastore {
    /**
     * Creates a reference to the entity (using the current DB -can be null-,
     * the collectionName, and id).
     */
    <T, V> DBRef createRef(Class<T> clazz, V id);

    /**
     * Creates a reference to the entity (using the current DB -can be null-,
     * the collectionName, and id).
     */
    <T> DBRef createRef(T entity);

    /**
     * Find the given entity (by collectionName/id).
     */
    <T> T get(Class<T> clazz, DBRef ref);

    /**
     * Gets the count this kind.
     */
    long getCount(String kind);

    /**
     *
     * @param kind
     * @param clazz
     * @param id
     * @param <T>
     * @param <V>
     * @return
     */
    <T, V> T get(String kind, Class<T> clazz, V id);

    /**
     *
     * @param kind
     * @param clazz
     * @param <T>
     * @return
     */
    <T> Query<T> find(String kind, Class<T> clazz);

    /**
     *
     * @param kind
     * @param clazz
     * @param property
     * @param value
     * @param offset
     * @param size
     * @param <T>
     * @param <V>
     * @return
     */
    <T, V> Query<T> find(String kind, Class<T> clazz, String property,
                         V value, int offset, int size);

    /**
     *
     * @param kind
     * @param entity
     * @param <T>
     * @return
     */
    <T> Key<T> save(String kind, T entity);

    <T> Iterable<Key<T>> save(Iterable<T> entities, WriteConcern wc);

    /**
     * Save the entity under a custom collection.
     *
     * @param entity
     * @param collection
     * @param <T>
     * @return
     * @since 1.3
     */
    <T> Key<T> save(T entity, DBCollection collection);

    /**
     * Save the entity under a customer collection and write concern.
     *
     * @param entity
     * @param collection
     * @param writeConcern
     * @param <T>
     * @return
     * @since 1.3
     */
    <T> Key<T> save(T entity, DBCollection collection, WriteConcern writeConcern);

    /**
     *
     * @param kind
     * @param clazz
     * @param id
     * @param <T>
     * @param <V>
     * @return
     */
    <T, V> WriteResult delete(String kind, Class<T> clazz, V id);

    <T> WriteResult delete(T entity, WriteConcern wc);

    <T> WriteResult delete(Query<T> query, WriteConcern wc);

    /**
     *
     * @param kind
     * @param entity
     * @param <T>
     * @return
     */
    <T> Key<T> insert(String kind, T entity);

    /**
     *
     * @param entity
     * @param <T>
     * @return
     */
    <T> Key<T> insert(T entity);

    /**
     *
     * @param entity
     * @param wc
     * @param <T>
     * @return
     */
    <T> Key<T> insert(T entity, WriteConcern wc);

    /**
     *
     * @param entities
     * @param <T>
     * @return
     */
    <T> Iterable<Key<T>> insert(@SuppressWarnings("unchecked") T... entities);

    /**
     *
     * @param entities
     * @param wc
     * @param <T>
     * @return
     */
    <T> Iterable<Key<T>> insert(Iterable<T> entities, WriteConcern wc);

    /**
     *
     * @param kind
     * @param entities
     * @param <T>
     * @return
     */
    <T> Iterable<Key<T>> insert(String kind, Iterable<T> entities);

    /**
     *
     * @param kind
     * @param entities
     * @param wc
     * @param <T>
     * @return
     */
    <T> Iterable<Key<T>> insert(String kind, Iterable<T> entities,
                                WriteConcern wc);

    /**
     *
     * @param kind
     * @param clazz
     * @param <T>
     * @return
     */
    <T> Query<T> createQuery(String kind, Class<T> clazz);

    /**
     * DBObject implementations; in case we don't have features impl'd yet.
     */
    <T> Query<T> createQuery(Class<T> kind, DBObject q);

    /**
     * @param kind
     * @param clazz
     * @param q
     * @param <T>
     * @return
     */
    <T> Query<T> createQuery(String kind, Class<T> clazz, DBObject q);

    /**
     * Returns a new query based on the example object.
     */
    <T> Query<T> queryByExample(String kind, T example);

    /**
     * @param kind
     * @param ops
     * @param <T>
     * @return
     */
    <T> UpdateOperations<T> createUpdateOperations(Class<T> kind,
                                                   DBObject ops);

    /**
     * @param fact
     * @return
     */
    DBDecoderFactory setDecoderFact(DBDecoderFactory fact);

    /**
     * @return
     */
    DBDecoderFactory getDecoderFact();
}