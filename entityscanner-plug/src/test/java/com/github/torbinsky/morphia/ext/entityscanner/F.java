/**
 * 
 */
package com.github.torbinsky.morphia.ext.entityscanner;

import org.bson.types.ObjectId;

import com.github.torbinsky.morphia.annotations.Entity;
import com.github.torbinsky.morphia.annotations.Id;

@Entity
class F {
	@Id
	ObjectId id;
}