package com.github.torbinsky.morphia.mapping.lazy;

import org.junit.Assert;

import org.junit.Test;

import com.github.torbinsky.morphia.Key;
import com.github.torbinsky.morphia.testutil.TestEntity;

public class TestCGLibLazyProxyFactory extends ProxyTestBase
 {
	@Test
	public final void testCreateProxy()
	{
		final E e = new E();
		e.setFoo("bar");
		final Key<E> key = ds.save(e);
		E eProxy = new CGLibLazyProxyFactory().createProxy(E.class, key,
				new DefaultDatastoreProvider());

		assertNotFetched(eProxy);
		Assert.assertEquals("bar", eProxy.getFoo());
		assertFetched(eProxy);

		eProxy = deserialize(eProxy);
		assertNotFetched(eProxy);
		Assert.assertEquals("bar", eProxy.getFoo());
		assertFetched(eProxy);

	}
	public static class E extends TestEntity
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String foo;

		public void setFoo(final String string)
		{
			foo = string;
		}

		public String getFoo()
		{
			return foo;
		}
	}

}
