package com.rzb.pms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtherInfoDTO {

	private String customerName;

	private String customerMobileNumber;

	private String customerEmail;

	private String paymentMode;

	private Boolean isInvoiceRequired;

	private float discount;

	private String invoiceType;// print invoice or email invoice

}
