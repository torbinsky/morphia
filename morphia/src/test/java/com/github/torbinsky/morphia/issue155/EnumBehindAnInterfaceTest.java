package com.github.torbinsky.morphia.issue155;

import org.junit.Assert;

import org.junit.Ignore;
import org.junit.Test;

import com.github.torbinsky.morphia.TestBase;

/**
 * @author josephpachod
 */
public class EnumBehindAnInterfaceTest extends TestBase {
	@Test @Ignore("does not work since the EnumConverter stores as a single string value -- no type info")
	public void testEnumBehindAnInterfacePersistence() throws Exception {
		morphia.map(ContainerEntity.class);
		ContainerEntity n = new ContainerEntity();
		ds.save(n);
		n = ds.get(n);
		Assert.assertSame(EnumBehindAnInterface.A, n.foo);
	}
}
