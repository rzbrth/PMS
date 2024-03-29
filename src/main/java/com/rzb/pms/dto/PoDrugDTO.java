package com.rzb.pms.dto;

import java.util.Date;

import com.rzb.pms.model.PoLineItems;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PoDrugDTO {

	private Integer poDrugId;

	private String drugName;

	private String drugDescription;

	private Double drugQuantity;

	private float drugPrice;

	private String drugId;

	private Integer poId;

	private Integer distributerId;
	
	// below two properties only to be used while creating stock from po directly
	private Date expireDate;
	
	private String location;
	
	public PoDrugDTO(PoLineItems data) {
		this.distributerId = data.getDistributerId();
		this.drugDescription = data.getDrugDescription();
		this.drugId = data.getDrugId();
		this.drugName = data.getDrugName();
		this.drugPrice = data.getDrugPrice();
		this.drugQuantity = data.getDrugQuantity();
		this.poDrugId = data.getPoDrugId();
		this.poId = data.getPoId();
	}
	
	public PoDrugDTO(PoDrugDTO data) {
		this.distributerId = data.getDistributerId();
		this.drugDescription = data.getDrugDescription();
		this.drugId = data.getDrugId();
		this.drugName = data.getDrugName();
		this.drugPrice = data.getDrugPrice();
		this.drugQuantity = data.getDrugQuantity();
		this.poDrugId = data.getPoDrugId();
		this.poId = data.getPoId();
	}
}
