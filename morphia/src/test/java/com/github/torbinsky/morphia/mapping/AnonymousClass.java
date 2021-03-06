/**
 * 
 */
package com.github.torbinsky.morphia.mapping;

import java.io.Serializable;

import org.junit.Assert;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.github.torbinsky.morphia.AdvancedDatastore;
import com.github.torbinsky.morphia.Key;
import com.github.torbinsky.morphia.TestBase;
import com.github.torbinsky.morphia.annotations.Embedded;
import com.github.torbinsky.morphia.annotations.Id;

/**
 * @author scott hernandez
 */
@SuppressWarnings("unused")
public class AnonymousClass extends TestBase{
	
	@Embedded
	private static class CId implements Serializable {
		static final long serialVersionUID = 1L;
		ObjectId id = new ObjectId();
		String name;		
		CId() {}
		CId(String n) {name=n;}
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof CId)) return false;
			CId other = ((CId)obj);
				return other.id.equals(id) && other.name.equals(name);
		}
		
	}

	private static class E {
		@Id CId id;
		String e;
	}
	

	@Test
	public void testMapping() throws Exception {
		E e = new E();
		e.id = new CId("test");
		
		Key<E> key = ds.save(e);
		e = ds.get(e);
		Assert.assertEquals("test", e.id.name);
		Assert.assertNotNull(e.id.id);
	}

	@Test
	public void testDelete() throws Exception {
		E e = new E();
		e.id = new CId("test");
		
		Key<E> key = ds.save(e);
		ds.delete(E.class, e.id);
	}

	@Test
	public void testOtherDelete() throws Exception {
		E e = new E();
		e.id = new CId("test");
		
		Key<E> key = ds.save(e);
		((AdvancedDatastore)ds).delete(ds.getCollection(E.class).getName(), E.class, e.id);
	}

}
