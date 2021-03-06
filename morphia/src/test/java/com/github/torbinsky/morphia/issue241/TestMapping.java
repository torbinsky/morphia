package com.github.torbinsky.morphia.issue241;

import java.net.UnknownHostException;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.torbinsky.morphia.Datastore;
import com.github.torbinsky.morphia.DatastoreImpl;
import com.github.torbinsky.morphia.Morphia;
import com.github.torbinsky.morphia.annotations.Entity;
import com.github.torbinsky.morphia.annotations.Id;
import com.github.torbinsky.morphia.dao.BasicDAO;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;


/**
 * Unit test for testing morphia mappings with generics.
 */
public class TestMapping {

    Morphia morphia = new Morphia();

    Mongo mongo;
    Datastore datastore;


    @Before
    public void setUp() {
        try {
            mongo = new MongoClient(new MongoClientURI("mongodb://127.0.0.1:27017"));
            datastore = new DatastoreImpl(morphia, mongo, "MY_DB");
        } catch (UnknownHostException unknownHostException) {
        } catch (MongoException mongoException) {
        }
    }

    @After
    public void tearDown() {
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testMapping() {
        BasicDAO<Message, ObjectId> messageDAO = new BasicDAO<Message, ObjectId>(Message.class, datastore);
        Assert.assertNotNull(messageDAO);
    }

    @SuppressWarnings("unused")
    @Entity
    private static class Message<U extends User> {

        @Id
        private ObjectId id;
        private U user;

        public U getUser() {
            return user;
        }

        public void setUser(U user) {
            this.user = user;
        }
    }

    @Entity
    private static class User {
        @Id
        private ObjectId id;

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final User other = (User) obj;
            if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
            return hash;
        }
    }
}
