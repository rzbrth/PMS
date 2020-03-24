package com.rzb.pms.rsql;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum SearchKey {

	GENERIC_NAME("genericName"), DRUG_NAME("brandName"), COMPOSITION("composition"), COMPANY("company"),
	LOCATION("location");

	String name;

	private SearchKey(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static SearchKey getKeyData(String key) {

		if (SearchKey.DRUG_NAME.getName().equalsIgnoreCase(key)) {
			return DRUG_NAME;
		} else if (SearchKey.GENERIC_NAME.getName().equalsIgnoreCase(key)) {
			return GENERIC_NAME;
		} else if (SearchKey.COMPOSITION.getName().equalsIgnoreCase(key)) {
			return COMPOSITION;
		} else if (SearchKey.COMPANY.getName().equalsIgnoreCase(key)) {
			return COMPANY;
		} else if (SearchKey.LOCATION.getName().equalsIgnoreCase(key)) {
			return LOCATION;

		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Please Provide right Search parameter . Like genericName, brandName, composition, company, location");
		}

	}
}
