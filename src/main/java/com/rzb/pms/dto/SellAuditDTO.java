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
public class SellAuditDTO {

	private int sellAuditId;

	private String drugId;

	private String cartId;

	private String genericName;

	private String composition;

	private String brandName;

	private Date sellDate;

	private String sellBy;

	private Integer packing;

	private float mrp;

	private Float unitPrice;

	private String location;

	private Double itemSellQuantity;

	private float itemSellPrice;

	private float discount;

	private String paymentMode;
}
