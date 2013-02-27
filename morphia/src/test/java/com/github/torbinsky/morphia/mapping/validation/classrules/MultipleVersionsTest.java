/**
 * 
 */
package com.github.torbinsky.morphia.mapping.validation.classrules;

import org.junit.Test;

import com.github.torbinsky.morphia.TestBase;
import com.github.torbinsky.morphia.annotations.Version;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolationException;
import com.github.torbinsky.morphia.testutil.AssertedFailure;
import com.github.torbinsky.morphia.testutil.TestEntity;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 *
 */
public class MultipleVersionsTest extends TestBase {
	
	public static class Fail1 extends TestEntity {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@Version
		long v1;
		@Version
		long v2;
	}

	public static class OK1 extends TestEntity {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@Version
		long v1;
	}
	
	@Test
	public void testCheck() {
		new AssertedFailure(ConstraintViolationException.class) {
			public void thisMustFail() throws Throwable {
				morphia.map(Fail1.class);
			}
		};
		morphia.map(OK1.class);
	}
	
}
