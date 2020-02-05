package com.rzb.pms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DrugDispenseDTO {

	private String drugId;

	private Double itemSellQuantity;

	private String drugUnit;// Strip or trimmed
	
	private Boolean isDiscountApplicable;

}
