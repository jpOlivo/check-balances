package com.nu.exception;

@SuppressWarnings("serial")
public class ResourceLockingFailureException extends RuntimeException {

	public ResourceLockingFailureException(Long version) {
		super("Can't update resource with version {" + version + "}. Resource already changed!");
	}
}
