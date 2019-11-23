package com.rzb.pms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DrugDTO {

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

}
