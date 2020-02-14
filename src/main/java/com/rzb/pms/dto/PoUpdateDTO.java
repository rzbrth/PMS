package com.rzb.pms.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PoUpdateDTO {

	private List<PoLineItemUpdateDTO> updateLineItems;
	
	private Integer distributerId;
	
	//private Integer poDrugId;

	private Integer poId;
	
	private String poStatus;
	
	

}
