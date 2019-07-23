package com.nu.exception;

@SuppressWarnings("serial")
public class IfMatchErrorException extends RuntimeException {
	
	public IfMatchErrorException() {
		super("The value of field If-Match provided on Header doesn’t match with the current resource’s version.");
	}
}
