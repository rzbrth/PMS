package com.rzb.pms.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DispenseResponseDTO {

	private Integer dispenseId;

	private Integer customerId;

	private String customerName;

	private String mobileNumber;

	private String sellInvoiceNumber;

	private LocalDate sellDate;

	private String sellBy;

	private String paymentMode;

	private List<DispenseLineItemsDTO> dispenseLineItems;

	public DispenseResponseDTO(DispenseResponseDTO d) {

		this.customerId = d.getCustomerId();
		this.customerName = d.getCustomerName();
		this.dispenseId = d.getCustomerId();
		this.dispenseLineItems = d.getDispenseLineItems();
		this.mobileNumber = d.getMobileNumber();
		this.paymentMode = d.getPaymentMode();
		this.sellBy = d.getSellBy();
		this.sellDate = d.getSellDate();
		this.sellInvoiceNumber = d.getSellInvoiceNumber();

	}
}
