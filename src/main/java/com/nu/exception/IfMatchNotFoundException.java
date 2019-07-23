package com.nu.exception;

@SuppressWarnings("serial")
public class IfMatchNotFoundException extends RuntimeException {
	
	public IfMatchNotFoundException() {
		super("You must provide on If-Match field of Header the value that was received on ETag field of a previous request.");
	}
}
