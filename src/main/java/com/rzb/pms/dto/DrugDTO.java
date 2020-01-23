package com.rzb.pms.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.rzb.pms.model.Drug;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DrugDTO {

	private String drugId;

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

	private String drugForm;

	public DrugDTO() {
	}

	public DrugDTO(Drug data) {
		this.drugId = data.getDrugId();
		this.genericName = data.getGenericName();
		this.brandName = data.getBrandName();
		this.composition = data.getComposition();
		this.company = data.getCompany();
		this.packing = data.getPacking();
		this.mrp = data.getMrp();
		this.unitPrice = data.getUnitPrice();
		this.genericId = data.getGenericId();
		this.expiryDate = data.getExpiryDate();
	}

}
