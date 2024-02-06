package com.avega.portfolio.exception;

public class ApiException extends RuntimeException {

	private static final long serialVersionUID = 4450946379027482577L;

	public ApiException(String message) {
		super(message);
	}

	public ApiException() {
		super();
	}
}
