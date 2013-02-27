/**
 *
 */
package com.github.torbinsky.morphia.issue45;


import org.junit.Test;

import com.github.torbinsky.morphia.TestBase;
import com.github.torbinsky.morphia.annotations.Embedded;
import com.github.torbinsky.morphia.annotations.Entity;
import com.github.torbinsky.morphia.annotations.Transient;
import com.github.torbinsky.morphia.testutil.TestEntity;
import org.junit.Assert;

public class TestEmptyEntityMapping extends TestBase
{
    @Entity
    static class A extends TestEntity{
    	private static final long serialVersionUID = 1L;
		@Embedded
		B b;
    }

    @Embedded
    static class B
    {
        @Transient
        String foo;
    }

    @Test
    public void testEmptyEmbeddedNotNullAfterReload() throws Exception
    {
        A a = new A();
        a.b = new B();

        this.ds.save(a);
        Assert.assertNotNull(a.b);

        a = this.ds.find(A.class, "_id", a.getId()).get();
        Assert.assertNull(a.b);
    }
}
