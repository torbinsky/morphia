package com.github.torbinsky.morphia.query;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;

import com.github.torbinsky.morphia.TestBase;
import com.github.torbinsky.morphia.annotations.Id;
import com.mongodb.DBCollection;

/**
 * Created with IntelliJ IDEA.
 * User: jamesgreen
 * Date: 29/05/2013
 * Time: 21:02
 * To change this template use File | Settings | File Templates.
 */
public class CustomCollection extends TestBase {

    @Test
    public void testDifferentCollection() {
        morphia.map(A.class);

        Query<A> q = ds.createQuery(A.class);
        DBCollection custom = db.getCollection("CustomA");
        q.setDbCollection(custom);

        ds.delete(q);

        A a = new A();
        ads.save(a, db.getCollection("CustomA"));

        A found = q.get();
        Assert.assertNotNull(found);
        Assert.assertEquals(1, q.asList().size());
    }

    static class A {
        @Id
        ObjectId id;

    }
}
