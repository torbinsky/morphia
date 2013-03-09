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
		super(clazz, mapr);
		this.tenantProvider = tenant;
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
	public void validate() {
		super.validate();
	}
	
	
}
