package com.rzb.pms.model;

import java.time.LocalDate;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrugDispense {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer dispenseId;

	private String mobileNumber;

	private String drugId;

	private String genericName;

	private String composition;

	private String brandName;

	private LocalDate sellDate;

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

	private LocalDate expiryDate;

	private String drugForm;

	private String customerName;

	private String sellInvoiceNumber;

}
