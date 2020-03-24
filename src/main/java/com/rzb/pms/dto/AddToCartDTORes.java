package com.rzb.pms.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddToCartDTORes {

	private Double itemSellQuantity;

	private String drugUnit;// Strip or trimmed

	private Float gstPercentage;
	
	private Float discountPercentge;
	
	private Float priceBeforeTaxAndDiscount;

	private Float sellPriceAfterTaxAndDiscount;
	
	private Float mrp;
	
	private Double avlQntyWhole;
	
	private Double avlQntyTrimmed;

	private String brandName;
	
	private String location;
	
	private LocalDate expiryDate;
	
	private Integer stockId;
	
	private String drugId;
	
	private String drugForm;
	
	private Integer packing;


}
