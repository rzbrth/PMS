package com.rzb.pms.dto;

import java.util.Date;

import com.rzb.pms.model.Drug;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DrugDtoReqRes {

	private String genericName;

	private String brandName;

	private String composition;

	private String company;

	private Integer packing;

	private Float mrp;

	private Float unitPrice;

	private String genericId;

	private String location;

	private Date expiryDate;

	private Double avlQntyInWhole;

	private Double avlQntyInTrimmed;

	private String drugForm;

	public DrugDtoReqRes() {
	}

	public DrugDtoReqRes(Drug data) {
		this.genericName = data.getGenericName();
		this.brandName = data.getBrandName();
		this.composition = data.getComposition();
		this.company = data.getCompany();
		this.packing = data.getPacking();
		this.mrp = data.getMrp();
		this.unitPrice = data.getUnitPrice();
		this.genericId = data.getGenericId();
		this.location = data.getLocation();
		this.expiryDate = data.getExpiryDate();
		this.avlQntyInWhole = data.getAvlQntyInWhole();
		this.avlQntyInTrimmed = data.getAvlQntyInTrimmed();
	}
}
