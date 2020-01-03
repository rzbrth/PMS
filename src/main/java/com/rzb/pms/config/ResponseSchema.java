package com.rzb.pms.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Generic response schema for all the responses
 * 
 * @author Rajib Rath
 */
@Setter
@Getter
public class ResponseSchema<T> {

	private int statusCode;

	private String message;

	private T data;

}
