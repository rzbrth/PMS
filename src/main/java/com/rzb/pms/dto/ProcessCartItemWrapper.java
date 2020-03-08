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
public class ProcessCartItemWrapper {
	
	private List<AddToCartDTORes> item;

	private String customerName;

	private String customerMobileNumber;

	private String customerEmail;

	private String paymentMode;

	private Boolean isInvoiceRequired;

	private String invoiceType;
	
	
	

}
