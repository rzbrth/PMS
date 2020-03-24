package com.rzb.pms.projection;

import java.time.LocalDate;

import lombok.Value;

@Value
public class StockProjection {
	
	Integer stockId;

	String drugName;

	LocalDate expiryDate;
}
