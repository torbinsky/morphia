package com.github.torbinsky.morphia.mapping;

import javax.inject.Provider;

import com.github.torbinsky.morphia.tenant.IsTenant;
import com.github.torbinsky.morphia.utils.InvalidTenantCollectionIdentifierException;

/**
 * 
 * 
 * @author torbinsky
 *
 */
public class TenantMappedClass extends MappedClass {

	private Provider<IsTenant> tenantProvider;

	public TenantMappedClass(Provider<IsTenant> tenant, Class<?> clazz, DefaultMapper mapr) {
		super();
		this.clazz = clazz;
		this.mapr = mapr;
		this.tenantProvider = tenant;
		super.init();
	}

	@Override
	public String getCollectionName() {
		IsTenant tenant = tenantProvider.get();
		
		if (tenant.getCollectionSuffix() == null) {
			throw new InvalidTenantCollectionIdentifierException("Tenant[" + tenant.getCollectionSuffix() + "] has a null identifier.");
		}
		
		String collectionBaseName = super.getCollectionName();
		String collectionSuffix = tenantProvider.get().getCollectionSuffix();
		
		return collectionBaseName + "_" + collectionSuffix;
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public void validate() {
		super.validate();
	}
	
	
}
