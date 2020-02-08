package com.rzb.pms.dto;

import java.util.Date;
import java.util.List;

import com.rzb.pms.model.PurchaseOrderLineItems;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseOrderLineItemsDTO {

	private Integer poLId;

	private Date createdDate;

	private Date updatedDate;

	private String createdBy;

	private String updatedBy;
	
	private String poStatus;
	
	private String poReference;


	private List<PurchaseOrderDTO> poLineItem;
	
	public PurchaseOrderLineItemsDTO(PurchaseOrderLineItems data) {
		
		this.poLId = data.getPoLId();
		this.createdDate = data.getCreatedDate();
		this.updatedDate = data.getUpdatedDate();
		this.updatedBy = data.getUpdatedBy();
		this.createdBy = data.getUpdatedBy();
	}

}
