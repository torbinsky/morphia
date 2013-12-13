package com.github.torbinsky.morphia.mapping;

import java.lang.annotation.Annotation;

import com.github.torbinsky.morphia.annotations.TenantEntity;
import com.github.torbinsky.morphia.mapping.lazy.proxy.ProxyHelper;
import com.github.torbinsky.morphia.tenant.IsTenant;
import com.github.torbinsky.morphia.utils.InvalidTenantCollectionIdentifierException;
import com.github.torbinsky.morphia.utils.NoTenantAvailableException;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * An extension of the {@link DefaultMapper} that is tenant aware to properly map collection
 * names to a {@link IsTenant} if it is in scope and an entity is annotated as a {@link TenantEntity}
 * 
 * @author twerner
 * 
 */
public class TenantMapper extends DefaultMapper {
	private Provider<IsTenant> tenantProvider;

	@Inject
	public TenantMapper(Provider<IsTenant> tenantProvider) {
	    super();
		this.tenantProvider = tenantProvider;
	}
	
	

	public TenantMapper(Provider<IsTenant> tenantProvider, MapperOptions opts) {
        super(opts);
        this.tenantProvider = tenantProvider;
    }



    @SuppressWarnings("rawtypes")
	@Override
	public MappedClass addMappedClass(Class c) {
		return addMappedClass(mapClass(c), true);
	}

	@Override
	public MappedClass getMappedClass(final Object obj) {
		if (obj == null) {
			return null;
		}

		@SuppressWarnings("rawtypes")
		Class type = (obj instanceof Class) ? (Class) obj : obj.getClass();
		if (ProxyHelper.isProxy(obj)) {
			type = ProxyHelper.getReferentClass(obj);
		}

		MappedClass mc = getMCMap().get(type.getName());
		if (mc == null) {
			mc = mapClass(type);
			addMappedClass(mc, false);
		}

		return mc;
	}
	
	

	private MappedClass mapClass(@SuppressWarnings("rawtypes") Class c) {
		// Check if the Class is annotated to be Tenant-specific
		boolean isTenantSpecific = false;
		for (Annotation a : c.getAnnotations()) {
			if (a.annotationType().equals(TenantEntity.class)) {
				isTenantSpecific = true;
				break;
			}
		}

		// If this class is tenant specific, we need to get the tenant and
		// append the tenant's collection id to the collection name
		if (isTenantSpecific) {
			// Get our tenant from scope
			IsTenant tenant = tenantProvider.get();
			if (tenant != null) {
				if (tenant.getCollectionSuffix() != null) {
					return new TenantMappedClass(tenantProvider, c, this);
				} else {
					throw new InvalidTenantCollectionIdentifierException("Tenant[" + tenant.getCollectionSuffix() + "] has a null identifier.");
				}
			} else {
				throw new NoTenantAvailableException("Entity [" + c + "] is tenant specific but no tenant is available.");
			}
		} else {
			return new MappedClass(c, this);
		}
	}

}
