package com.github.torbinsky.morphia.mapping;

import com.github.torbinsky.morphia.tenant.IsTenant;

/**
 * 
 * 
 * @author torbinsky
 *
 */
public class TenantMappedClass extends MappedClass {

	private IsTenant tenant;

	public TenantMappedClass(IsTenant tenant, Class<?> clazz, DefaultMapper mapr) {
		super(clazz, mapr);
		this.tenant = tenant;
	}

	@Override
	public String getCollectionName() {
		return super.getCollectionName() + "_" + tenant.getCollectionSuffix();
	}

	@Override
	public void validate() {
		super.validate();
	}
	
	
}
