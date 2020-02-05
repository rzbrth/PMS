package com.rzb.pms.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellAudit {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer sellAuditId;

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

	private String customerMobileNumber;

	private Double reqQntyInWhole;

	private Double reqQntyInTrimmed;

	private Date expiryDate;

	private String drugForm;

	private Integer customerId;
 

}
