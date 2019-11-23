package com.rzb.pms.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Drugs {

	@Id
	private String drugCode;

	private String drugName;

	private String itemDescription;

	private String drug;

	private String genericName;

	private String drugStrength;

	private String drugForm;

	private String strength;

	private String unit;

	private String remarks;

	private String adminstrationRoute;

	private String adminstrationForm;
	
	//private String genericsCode;

	@ManyToOne
	@JoinColumn(name="genCodes")
	private Generics generics;

}
