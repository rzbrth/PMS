package com.rzb.pms.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
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

	private String genericId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date expiryDate;

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
		this.genericId = data.getGenericId();
		this.expiryDate = data.getExpiryDate();
	}
}
