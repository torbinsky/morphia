package com.github.torbinsky.morphia.tenant;

/**
 * A tenant provides a unique collection suffix to identify it's collections from other tenants' collections
 * 
 * @author torbinsky
 *
 */
public interface IsTenant {

	/**
	 * @return a unique, String collection identifier suffix to add to the collection name.
	 */
	String getCollectionSuffix();

}
