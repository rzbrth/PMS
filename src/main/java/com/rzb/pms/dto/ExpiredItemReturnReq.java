package com.rzb.pms.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpiredItemReturnReq {

	private Integer stockId;

	private String drugId;

	private String drugName;

	private Integer packing;

	private Float mrp;

	private Float unitPrice;

	private String location;

	private LocalDate expiryDate;

	private Integer distributerId;

	private String invoiceReference;

	private Integer poId;
}
