package com.rzb.pms.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockDirectRequestDTO {

	private String drugId;

	private String genericId;

	private Integer distributerId;

	private Integer packing;

	private Float mrp;

	private Float unitPrice;

	private String location;

	private Date expiryDate;

	private Double avlQntyWhole;

	private Double avlQntyTrimmed;
	
}
