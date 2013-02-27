package com.github.torbinsky.morphia.utils;

/**
 * Thrown when no tenant is available during a tenant-specific operation.
 * 
 * @author torbinsky
 *
 */
public class NoTenantAvailableException extends RuntimeException {

	public NoTenantAvailableException() {
		super();
	}

	public NoTenantAvailableException(String message) {
		super(message);
	}

}
