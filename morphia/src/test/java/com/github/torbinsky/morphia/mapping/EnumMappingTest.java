/**
 * 
 */
package com.github.torbinsky.morphia.mapping;

import org.junit.Assert;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.github.torbinsky.morphia.TestBase;
import com.github.torbinsky.morphia.annotations.Id;
import com.github.torbinsky.morphia.annotations.PreSave;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 *
 */
public class EnumMappingTest extends TestBase {
	public static class ContainsEnum {
		@Id ObjectId id;
		Foo foo = Foo.BAR;
		
		@PreSave
		void testMapping() {
			
		}
	}
	static enum Foo {
		BAR() {
		},
		BAZ
	}
	@Test
	public void testEnumMapping() throws Exception {
		morphia.map(ContainsEnum.class);
		
		ds.save(new ContainsEnum());
		Assert.assertEquals(1, ds.createQuery(ContainsEnum.class).field("foo").equal(Foo.BAR).countAll());
		Assert.assertEquals(1, ds.createQuery(ContainsEnum.class).filter("foo", Foo.BAR).countAll());
		Assert.assertEquals(1, ds.createQuery(ContainsEnum.class).disableValidation().filter("foo", Foo.BAR).countAll());
	}
	
	
}
