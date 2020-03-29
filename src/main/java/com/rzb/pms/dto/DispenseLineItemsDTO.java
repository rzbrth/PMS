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
public class DispenseLineItemsDTO {

	private Integer dispenseLineItemId;

	private String drugId;

	private String brandName;

	private Integer packing;

	private float mrp;

	private Float unitPrice;

	private String location;

	private Double itemSellQuantity;

	private float itemSellPrice;

	private float discount;

	private String drugUnit;// Strip or trimmed

	private LocalDate expiryDate;

	private String drugForm;

	private Integer dispenseId;

	public DispenseLineItemsDTO(DispenseLineItemsDTO d) {

		this.brandName = d.getBrandName();
		this.discount = d.getDiscount();
		this.dispenseId = d.getDispenseId();
		this.dispenseLineItemId = d.getDispenseLineItemId();
		this.drugForm = d.getDrugForm();
		this.drugId = d.getDrugForm();
		this.drugUnit = d.getDrugUnit();
		this.expiryDate = d.getExpiryDate();
		this.itemSellPrice = d.getItemSellPrice();
		this.itemSellQuantity = d.getItemSellQuantity();
		this.location = d.getLocation();
		this.mrp = d.getMrp();
		this.packing = d.getPacking();
		this.unitPrice = d.getUnitPrice();
	}

}
