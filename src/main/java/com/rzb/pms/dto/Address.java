package com.rzb.pms.dto;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class Address {
	/*
	 * We can have column name here if we want to define @Column(name = "name_jfb")
	 */
	@Column(name = "PRESENT_STATE")
	private String state;

	@Column(name = "PRESENT_CITY")
	private String city;

	@Column(name = "PRESENT_STREET")
	private String street;

	@Column(name = "PRESENT_PIN")
	private String pincode;

}
