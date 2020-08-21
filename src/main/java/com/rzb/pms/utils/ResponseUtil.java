package com.rzb.pms.utils;

import org.springframework.http.ResponseEntity;

import com.rzb.pms.config.ResponseSchema;

/**
 * @author Rajib.Rath
 *
 */
public class ResponseUtil {

	private ResponseUtil() {
	}

	public static <T> ResponseSchema<T> buildSuccessResponse(T result, ResponseSchema<T> responseSchema) {
		responseSchema.setStatusCode(200);
		responseSchema.setMessage("success");
		responseSchema.setResult(result);
		return responseSchema;
	}

	public static <T> ResponseSchema<T> buildFailedResponse(String failedMessage, T result,
			ResponseSchema<T> responseSchema) {
		responseSchema.setStatusCode(200);
		responseSchema.setMessage(failedMessage);
		responseSchema.setResult(result);
		return responseSchema;
	}

}
