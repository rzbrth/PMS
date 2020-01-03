package com.rzb.pms.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToCart {

	@Id
	private String mobileNumber;

	private String drugId;

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

	private String drugUnit;// Strip or trimmed

	private Date expiryDate;

	private String drugForm;

}
