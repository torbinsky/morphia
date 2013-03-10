/**
 *
 */
package com.github.torbinsky.morphia.mapping;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;

import com.github.torbinsky.morphia.TestBase;
import com.github.torbinsky.morphia.annotations.Id;

/**
 * @author ScottHernandez
 *
 */
public class URIMappingTest extends TestBase {

	private static class ContainsURI {
		@Id ObjectId id;
		URI uri;
	}
	private static class ContainsURIKeyedMap {
		@Id ObjectId id;
		Map<URI, String> uris = new HashMap<URI, String>();
	}


	@Test
	public void testURIField() throws Exception {
		ContainsURI entity = new ContainsURI();
		URI testURI = new URI("http://lamest.local/test.html");

		entity.uri= testURI;
		ds.save(entity);
		ContainsURI loaded = ds.find(ContainsURI.class).get();
		Assert.assertNotNull(loaded.uri);
		Assert.assertEquals(testURI, loaded.uri);

	}
	@Test
	public void testURIMap() throws Exception {
		ContainsURIKeyedMap entity = new ContainsURIKeyedMap();
		URI testURI = new URI("http://lamest.local/test.html");

		entity.uris.put(testURI, "first");
		ds.save(entity);
		ContainsURIKeyedMap loaded = ds.find(ContainsURIKeyedMap.class).get();
		Assert.assertNotNull(loaded.uris);
		Assert.assertEquals(1, loaded.uris.size());
		Assert.assertEquals(testURI, loaded.uris.keySet().iterator().next());

	}
}
