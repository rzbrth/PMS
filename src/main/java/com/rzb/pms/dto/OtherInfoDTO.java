package com.rzb.pms.dto;

import lombok.Data;

@Data
public class OtherInfoDTO {

	private String customerName;

	private String customerMobileNumber;

	private String customerEmail;

	private String paymentMode;

	private Boolean isInvoiceRequired;
	
	private float discount;

	private String invoiceType;// print invoice or email invoice

}
