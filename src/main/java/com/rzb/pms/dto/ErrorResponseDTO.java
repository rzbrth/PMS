package com.rzb.pms.dto;

/**
 *
 * @author Rajib
 * @param <T>
 */
public class ErrorResponseDTO<T> {

	private T message;

	public T getMessage() {
		return message;
	}

	public void setMessage(T message) {
		this.message = message;
	}

	public ErrorResponseDTO(T message) {
		this.message = message;
	}

}