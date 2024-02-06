package com.avega.portfolio.advice;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorMessage {
	private int statusCode;
	private LocalDateTime timestamp;
	private String message;
	private String description;

	public ErrorMessage(int statusCode, LocalDateTime timestamp, String message, String description) {
		this.statusCode = statusCode;
		this.timestamp = timestamp;
		this.message = message;
		this.description = description;
	}
}
