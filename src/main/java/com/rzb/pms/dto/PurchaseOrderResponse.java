package com.rzb.pms.dto;

import java.time.LocalDate;
import java.util.List;

import com.rzb.pms.model.PurchaseOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseOrderResponse {

	private Integer poId;

	private LocalDate createdDate;

	private LocalDate updatedDate;

	private String createdBy;

	private String updatedBy;

	private String poStatus;

	private String referenceNumber;

	// private List<PurchaseOrderDTO> poData;
	private List<PoDrugDTO> poLineItem;

	public PurchaseOrderResponse(PurchaseOrder data) {

		this.poId = data.getPoId();
		this.createdDate = data.getCreatedDate();
		this.updatedDate = data.getUpdatedDate();
		this.updatedBy = data.getUpdatedBy();
		this.createdBy = data.getUpdatedBy();
	}

}
