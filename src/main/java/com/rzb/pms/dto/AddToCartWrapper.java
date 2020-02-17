package com.rzb.pms.dto;

import java.util.List;

import lombok.Data;

@Data
public class AddToCartWrapper {

	private List<DrugDispenseDTO> item;

	private String customerName;

	private String customerMobileNumber;

	private String customerEmail;

	private String paymentMode;

	private Boolean isInvoiceRequired;

	private float discount;

	private String invoiceType;
	// private OtherInfoDTO info;

}
