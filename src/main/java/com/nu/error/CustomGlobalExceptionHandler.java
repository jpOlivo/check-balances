package com.nu.error;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.nu.exception.IfMatchErrorException;
import com.nu.exception.IfMatchNotFoundException;
import com.nu.exception.ResourceLockingFailureException;
import com.nu.exception.TransactionNotAllowedException;
import com.nu.exception.UserAccountNotFoundException;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(UserAccountNotFoundException.class)
	public void handleNotFound(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.NOT_FOUND.value());
	}

	@ExceptionHandler(IfMatchErrorException.class)
	public void handleIfMatchError(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.PRECONDITION_FAILED.value());
	}

	@ExceptionHandler(IfMatchNotFoundException.class)
	public void handleIfMatchNotFound(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value());
	}

	@ExceptionHandler(ResourceLockingFailureException.class)
	public void handleOptimisticLockingFailure(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.CONFLICT.value());
	}

	@ExceptionHandler(TransactionNotAllowedException.class)
	public void handleTransactionNotAllowed(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.CONFLICT.value());
	}

}