package com.avega.portfolio.exception;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
public class UserAlreadyExistsException extends RuntimeException {

	private final String resourceName;

	private final String fieldName;

	private final String fieldValue;

	public UserAlreadyExistsException(String resourceName, String fieldName, String fieldValue) {
		super(String.format("%s Already Exists %s: %s", resourceName, fieldName, fieldValue));
		this.resourceName = resourceName;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}
}