package com.nu.exception;

@SuppressWarnings("serial")
public class UserAccountNotFoundException extends RuntimeException {

	public UserAccountNotFoundException(String name) {
		super("UserAccount with name '" + name + "' not found");
	}

}
