package com.rzb.pms.dto;

import java.util.List;

import lombok.Data;

@Data
public class AddToCartWrapper {

	private List<DrugDispenseDTO> item;

	private OtherInfoDTO info; 	
	
	
}
