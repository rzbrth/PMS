package com.rzb.pms.dto;

import org.springframework.http.HttpStatus;

import com.rzb.pms.exception.CustomException;

public enum DrugType {

	STRIP, TRIMMED, VIAL, INJ, TOPICAL, SYRUP, SACHET, TABLET, CAPSULE, INHALATION;

	@Override
	public String toString() {
		return super.toString();
	}

	public static DrugType getDrugType(String type) {

		if (DrugType.STRIP.toString().equalsIgnoreCase(type)) {
			return STRIP;
		} else if (DrugType.TRIMMED.toString().equalsIgnoreCase(type)) {
			return TRIMMED;
		} else if (DrugType.VIAL.toString().equalsIgnoreCase(type)) {
			return VIAL;
		} else if (DrugType.INJ.toString().equalsIgnoreCase(type)) {
			return INJ;
		} else if (DrugType.TOPICAL.toString().equalsIgnoreCase(type)) {
			return TOPICAL;
		} else if (DrugType.SYRUP.toString().equalsIgnoreCase(type)) {
			return SYRUP;
		} else if (DrugType.SACHET.toString().equalsIgnoreCase(type)) {
			return SACHET;
		} else if (DrugType.TABLET.toString().equalsIgnoreCase(type)) {
			return TABLET;
		} else if (DrugType.CAPSULE.toString().equalsIgnoreCase(type)) {
			return CAPSULE;
		} else if (DrugType.INHALATION.toString().equalsIgnoreCase(type)) {
			return INHALATION;

		} else {
			throw new CustomException("Please Provide right parameter", HttpStatus.BAD_REQUEST);
		}

	}
}
