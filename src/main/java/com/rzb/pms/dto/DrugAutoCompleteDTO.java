package com.rzb.pms.dto;

import java.util.Date;

import com.rzb.pms.model.Drug;
import com.rzb.pms.utils.BeanUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DrugAutoCompleteDTO {

	private String genericName;

	private String drugId;

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

	private Double avlQntyInWhole;// whole medecine

	private Double avlQntyInTrimmed;// medecine after cutting

	private String wholeAvlQntyInWords;

	public DrugAutoCompleteDTO(Drug x) {

		this.drugId = x.getDrugId();
		this.genericName = x.getGenericName();
		this.brandName = x.getBrandName();
		this.composition = x.getComposition();
		this.company = x.getCompany();
		this.packing = x.getPacking();
		this.mrp = x.getMrp();
		this.unitPrice = x.getUnitPrice();
		this.genericId = x.getGenericId();
		this.location = x.getLocation();
		this.expiryDate = x.getExpiryDate();
		this.avlQntyInTrimmed = x.getAvlQntyInTrimmed();
		this.avlQntyInWhole = x.getAvlQntyInWhole();
		this.drugForm = x.getDrugForm();
		if (avlQntyInWhole != null) {
			if (x.getAvlQntyInWhole() % 1 != 0) {
				this.wholeAvlQntyInWords = findQntyInWord(avlQntyInWhole, drugForm);

			} else {
				this.wholeAvlQntyInWords = BeanUtil.stripTrailingZero(String.valueOf(avlQntyInWhole)) + " "
						+ DrugType.STRIP.toString() + " " + "of" + " " + drugForm;
			}
		} else {
			this.wholeAvlQntyInWords = "Stock not available for this drug";
		}

	}

	private String findQntyInWord(Double avlQntyInWhole, String drugForm) {

		String param = String.valueOf(avlQntyInWhole);
		String LHS = param.split("\\.")[0];
		String RHS = param.split("\\.")[1];
		String lhsDrugForm = null;

		if (drugForm.equalsIgnoreCase(DrugType.CAPSULE.toString())
				|| drugForm.equalsIgnoreCase(DrugType.TABLET.toString())) {

			lhsDrugForm = DrugType.STRIP.toString();
			String result = LHS + " " + lhsDrugForm + " " + RHS + " " + getSuffix(drugForm);
			return result;

		} else {
			lhsDrugForm = drugForm;
			String result = LHS + " " + lhsDrugForm;
			return result;
		}

	}

	private String getSuffix(String drugForm) {

		if (drugForm.equalsIgnoreCase(DrugType.TABLET.toString())) {

			return DrugType.TABLET.toString();

		} else if (drugForm.equalsIgnoreCase(DrugType.CAPSULE.toString())) {

			return DrugType.CAPSULE.toString();
		} else {
			return drugForm;
		}

	}

}
