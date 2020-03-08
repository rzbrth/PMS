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
public class DrugDispenseWrapperDTO {

	private List<AddToCartDTORes> item;
	
	private String mobileNumber;
	
	private String name;
	
	private Boolean isInvoiceRequired;
	
	private String invoiceType;
	
	private String paymentMode;
	
	
	
}
