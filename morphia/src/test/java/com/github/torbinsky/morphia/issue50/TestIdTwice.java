package com.github.torbinsky.morphia.issue50;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.github.torbinsky.morphia.TestBase;
import com.github.torbinsky.morphia.annotations.Id;
import com.github.torbinsky.morphia.mapping.validation.ConstraintViolationException;
import com.github.torbinsky.morphia.testutil.TestEntity;

public class TestIdTwice extends TestBase {

	@Test
	public final void testRedundantId() {
		try {
			morphia.map(A.class);
			fail();
		} catch (ConstraintViolationException expected) {
			// fine
		}
	}

	public static class A extends TestEntity {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@Id
		String extraId;
		@Id
		String broken;
	}

}
