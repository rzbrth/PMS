package com.rzb.pms.utils;

import com.rzb.pms.config.ResponseSchema;

/**
 * @author Rajib.Rath
 *
 */
public class ResponseUtil {

	private ResponseUtil() {
	}

	public static <T> ResponseSchema<T> buildSuccessResponse(T data, ResponseSchema<T> responseSchema) {
		responseSchema.setStatusCode(200);
		responseSchema.setMessage("success");
		responseSchema.setData(data);
		return responseSchema;
	}

	public static <T> ResponseSchema<T> buildFailedResponse(String failedMessage, T data,
			ResponseSchema<T> responseSchema) {
		responseSchema.setStatusCode(200);
		responseSchema.setMessage(failedMessage);
		responseSchema.setData(data);
		return responseSchema;
	}
}
