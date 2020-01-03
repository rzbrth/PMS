package com.rzb.pms.dto;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.rzb.pms.model.Customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddToCartDTO {

	private Integer cartId;

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
	
	private Customer customer;
    
	private Date expiryDate;

	private String drugForm;
}
