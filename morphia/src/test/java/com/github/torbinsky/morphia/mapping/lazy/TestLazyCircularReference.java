package com.github.torbinsky.morphia.mapping.lazy;

import org.junit.Assert;

import org.junit.Test;

import com.github.torbinsky.morphia.Key;
import com.github.torbinsky.morphia.annotations.Reference;
import com.github.torbinsky.morphia.mapping.lazy.proxy.LazyReferenceFetchingException;
import com.github.torbinsky.morphia.mapping.lazy.proxy.ProxiedEntityReference;
import com.github.torbinsky.morphia.testutil.TestEntity;

public class TestLazyCircularReference extends ProxyTestBase {
	@Test
	public final void testCreateProxy() {
		RootEntity root = new RootEntity();
		ReferencedEntity reference = new ReferencedEntity();
		reference.parent = root;

		root.r = reference;
		root.r.setFoo("bar");

		ds.save(reference, root);

		root = ds.get(root);

		assertNotFetched(root.r);
		Assert.assertEquals("bar", root.r.getFoo());
		assertFetched(root.r);
		Assert.assertEquals("bar", root.r.getFoo());

		// now remove it from DB
		ds.delete(root.r);

		root = deserialize(root);
		assertNotFetched(root.r);

		try {
			// must fail
			root.r.getFoo();
			Assert.fail("Expected Exception did not happen");
		} catch (LazyReferenceFetchingException expected) {
			// fine
		}

	}

	@Test
	public final void testShortcutInterface() {
        RootEntity root = new RootEntity();
		ReferencedEntity reference = new ReferencedEntity();
		reference.parent = root;

		root.r = reference;
		reference.setFoo("bar");

		Key<ReferencedEntity> k = ds.save(reference);
		String keyAsString = k.getId().toString();
		ds.save(root);

		root = ds.get(root);

		ReferencedEntity p = root.r;

		assertIsProxy(p);
		assertNotFetched(p);
		Assert.assertEquals(keyAsString, ((ProxiedEntityReference) p).__getKey().getId().toString());
		// still unfetched?
		assertNotFetched(p);
		p.getFoo();
		// should be fetched now.
		assertFetched(p);

		root = deserialize(root);
		p = root.r;
		assertNotFetched(p);
		p.getFoo();
		// should be fetched now.
		assertFetched(p);

		root = ds.get(root);
		p = root.r;
		assertNotFetched(p);
		ds.save(root);
		assertNotFetched(p);
	}


	@Test
	public final void testSerialization() {
		RootEntity e1 = new RootEntity();
		ReferencedEntity e2 = new ReferencedEntity();
		e2.parent = e1;

		e1.r = e2;
		e2.setFoo("bar");

		ds.save(e2, e1);

		e1 = deserialize(ds.get(e1));

		assertNotFetched(e1.r);
		Assert.assertEquals("bar", e1.r.getFoo());
		assertFetched(e1.r);

		e1 = deserialize(e1);
		assertNotFetched(e1.r);
		Assert.assertEquals("bar", e1.r.getFoo());
		assertFetched(e1.r);

	}

	@Test
	public final void testGetKeyWithoutFetching() {
		RootEntity root = new RootEntity();
		ReferencedEntity reference = new ReferencedEntity();
		reference.parent = root;

		root.r = reference;
		reference.setFoo("bar");

		Key<ReferencedEntity> k = ds.save(reference);
		String keyAsString = k.getId().toString();
		ds.save(root);

		root = ds.get(root);

		ReferencedEntity p = root.r;

		assertIsProxy(p);
		assertNotFetched(p);
		Assert.assertEquals(keyAsString, ds.getMapper().getKey(p).getId().toString());
		// still unfetched?
		assertNotFetched(p);
		p.getFoo();
		// should be fetched now.
		assertFetched(p);

	}

	public static class RootEntity extends TestEntity {
		private static final long serialVersionUID = 1L;
		@Reference(lazy = true)
		ReferencedEntity r;
		@Reference(lazy = true)
		ReferencedEntity secondReference;

	}

	public static class ReferencedEntity extends TestEntity {
		private static final long serialVersionUID = 1L;
		private String foo;

		@Reference(lazy=true)
		RootEntity parent;

		public void setFoo(final String string) {
			foo = string;
		}

		public String getFoo() {
			return foo;
		}
	}

}
