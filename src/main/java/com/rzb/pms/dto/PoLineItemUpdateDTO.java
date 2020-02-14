package com.rzb.pms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PoLineItemUpdateDTO {

	private String drugId;

	private Double drugQuantity;

	private String drugName;

	private String drugDescription;

	private float drugPrice;

	private Integer poDrugId;

}
