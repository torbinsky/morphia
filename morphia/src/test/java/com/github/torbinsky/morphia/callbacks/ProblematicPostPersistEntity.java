package com.github.torbinsky.morphia.callbacks;

import org.bson.types.ObjectId;

import com.github.torbinsky.morphia.annotations.Id;
import com.github.torbinsky.morphia.annotations.PostPersist;

public class ProblematicPostPersistEntity {
	@Id
	ObjectId id;

	Inner i = new Inner();

	boolean called;

	@PostPersist
	void m1() {
		called = true;
	}

	static class Inner {
		boolean called;

		String foo = "foo";

		@PostPersist
		void m2() {
			called = true;
		}
	}
}
