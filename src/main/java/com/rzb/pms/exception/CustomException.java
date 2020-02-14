package com.rzb.pms.exception;

import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

/**
 * This is a generic exception used for throwing different types of error codes.
 * below is the usage throw new CustomException("user not found",
 * HttpStatus.BAD_REQUEST);
 * 
 * @author Rajib.rath
 * 
 *
 */
@Slf4j
public class CustomException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String message;
	private HttpStatus httpStatus;

	public CustomException() {
		super();
	}

	public CustomException(String message, HttpStatus httpStatus) {
		this.message = message;
		this.httpStatus = httpStatus;
		log.error(message, this);
	}

	public CustomException(String message, Throwable cause) {
		super(message, cause);
		log.error(message, this);
	}

	@Override
	public String getMessage() {
		return message;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}
