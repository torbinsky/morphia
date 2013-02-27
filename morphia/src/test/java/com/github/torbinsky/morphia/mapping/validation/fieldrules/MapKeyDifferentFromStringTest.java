/**
 * 
 */
package com.github.torbinsky.morphia.mapping.validation.fieldrules;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.github.torbinsky.morphia.TestBase;
import com.github.torbinsky.morphia.annotations.Embedded;
import com.github.torbinsky.morphia.annotations.Reference;
import com.github.torbinsky.morphia.annotations.Serialized;
import com.github.torbinsky.morphia.testutil.AssertedFailure;
import com.github.torbinsky.morphia.testutil.TestEntity;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * 
 */
@SuppressWarnings("unchecked")
public class MapKeyDifferentFromStringTest extends TestBase {
	
	public static class MapWithWrongKeyType1 extends TestEntity {
		private static final long serialVersionUID = 1L;
		@Serialized
		Map<Integer, Integer> shouldBeOk = new HashMap();
		
	}
	
	public static class MapWithWrongKeyType2 extends TestEntity {
		private static final long serialVersionUID = 1L;
		@Reference
		Map<Integer, Integer> shouldBeOk = new HashMap();
		
	}
	
	public static class MapWithWrongKeyType3 extends TestEntity {
		private static final long serialVersionUID = 1L;
		@Embedded
		Map<BigDecimal, Integer> shouldBeOk = new HashMap();
		
	}
	
	@Test
	public void testCheck() {
		morphia.map(MapWithWrongKeyType1.class);
		
		new AssertedFailure() {
			public void thisMustFail() throws Throwable {
				morphia.map(MapWithWrongKeyType2.class);
			}
		};
		
		new AssertedFailure() {
			public void thisMustFail() throws Throwable {
				morphia.map(MapWithWrongKeyType3.class);
			}
		};
	}
	
}
