/**
 *
 */
package com.github.torbinsky.morphia.mapping;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Assert;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.github.torbinsky.morphia.TestBase;
import com.github.torbinsky.morphia.annotations.Id;
import com.github.torbinsky.morphia.annotations.Property;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class ClassMappingTest extends TestBase
{

    public static class E {
		@Id ObjectId id;

        @SuppressWarnings("rawtypes")
		@Property Class<? extends Collection> testClass;
        @SuppressWarnings("rawtypes")
		Class<? extends Collection> testClass2;
    }

    @Test
    public void testMapping() throws Exception
    {
        E e = new E();

        e.testClass = LinkedList.class;
        ds.save(e);

        e = ds.get(e);
        Assert.assertEquals(LinkedList.class, e.testClass);
    }

    @Test
    public void testMappingWithoutAnnotation() throws Exception
    {
        E e = new E();

        e.testClass2 = LinkedList.class;
        ds.save(e);

        e = ds.get(e);
        Assert.assertEquals(LinkedList.class, e.testClass2);
    }

}
