package com.rzb.pms.dto;

import java.util.Date;
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
public class PurchaseOrderDTO {

	private Integer poId;

	private Date createdDate;

	private Date updatedDate;

	private String createdBy;

	private String updatedBy;
	
	private String poStatus;
	
	private String poReference;


	private List<PoDrugDTO> poLineItem;
	
	public PurchaseOrderDTO(PurchaseOrder data) {
		
		this.poId = data.getPoId();
		this.createdDate = data.getCreatedDate();
		this.updatedDate = data.getUpdatedDate();
		this.updatedBy = data.getUpdatedBy();
		this.createdBy = data.getUpdatedBy();
	}

}
