package com.nu.exception;

@SuppressWarnings("serial")
public class TransactionNotAllowedException extends RuntimeException {

	public TransactionNotAllowedException(String message) {
		super(message);
	}

}
