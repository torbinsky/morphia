package com.github.torbinsky.morphia.query;

import java.util.Collections;

import org.junit.Test;

import com.github.torbinsky.morphia.TestBase;
import com.github.torbinsky.morphia.TestMapping.BaseEntity;
import com.github.torbinsky.morphia.annotations.Entity;
import com.github.torbinsky.morphia.testutil.AssertedFailure;

public class TestQueryParams extends TestBase {
	@Entity
	static class E extends BaseEntity {
		
	}
	
	@Test
	public void testNullAcceptance() throws Exception {
		Query<E> q = ds.createQuery(E.class);
		final FieldEnd<?> e = q.field("_id");
		
		// have to suceed:
		e.equal(null);
		e.notEqual(null);
		e.hasThisOne(null);
		
		// have to fail:
		new AssertedFailure() {
			public void thisMustFail() throws Throwable {
				e.greaterThan(null);
			}
		};
		
		new AssertedFailure() {
			public void thisMustFail() throws Throwable {
				e.greaterThanOrEq(null);
			}
		};
		
		new AssertedFailure() {
			public void thisMustFail() throws Throwable {
				e.hasAllOf(null);
			}
		};
		
		new AssertedFailure() {
			public void thisMustFail() throws Throwable {
				e.hasAnyOf(null);
			}
		};
		
		new AssertedFailure() {
			public void thisMustFail() throws Throwable {
				e.hasNoneOf(null);
			}
		};
		
		new AssertedFailure() {
			public void thisMustFail() throws Throwable {
				e.hasThisElement(null);
			}
		};
		
		new AssertedFailure() {
			public void thisMustFail() throws Throwable {
				e.lessThan(null);
			}
		};
		
		new AssertedFailure() {
			public void thisMustFail() throws Throwable {
				e.lessThanOrEq(null);
			}
		};
		
		new AssertedFailure() {
			public void thisMustFail() throws Throwable {
				e.startsWith(null);
			}
		};

		new AssertedFailure() {
			public void thisMustFail() throws Throwable {
				e.startsWithIgnoreCase(null);
			}
		};
	}
	
	@Test
	public void testEmptyCollectionAcceptance() throws Exception {
		Query<E> q = ds.createQuery(E.class);
		final FieldEnd<?> e = q.field("_id");
		
		new AssertedFailure() {
			public void thisMustFail() throws Throwable {
				e.hasAllOf(Collections.EMPTY_LIST);
			}
		};
		
		new AssertedFailure() {
			public void thisMustFail() throws Throwable {
				e.hasNoneOf(Collections.EMPTY_LIST);
			}
		};
		
//		new AssertedFailure() {
//			public void thisMustFail() throws Throwable {
//				e.hasAnyOf(Collections.EMPTY_LIST);
//			}
//		};
	}
}
