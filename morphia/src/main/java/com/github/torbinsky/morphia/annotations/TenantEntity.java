package com.github.torbinsky.morphia.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.torbinsky.morphia.mapping.Mapper;
/**
 * Allows marking and naming a tenant-specific collection which will have a tenant identifier appended to it
 * @author Torben Werner
 */
@Documented @Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TenantEntity {
	String value() default Mapper.IGNORED_FIELDNAME;
	CappedAt cap() default @CappedAt(0);
	//@Deprecated //to be replaced. This is a temp hack until polymorphism and discriminators are impl'd
	boolean noClassnameStored() default false;
	
	//set slaveOk for queries for this Entity.
	boolean slaveOk() default false;
	
	//any WriteConcern static string. Case insensitive. STRICT/SAFE, NORMAL, etc...
	String concern() default "";
}

