package com.rzb.pms.dto;

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

	private String drugForm;

	private String wholeAvlQntyInWords;

}
