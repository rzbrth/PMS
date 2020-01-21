package com.rzb.pms.dto;

import java.util.Date;

import com.rzb.pms.model.PoDrug;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockDrugDTO {

	private Integer poDrugId;

	private String drugName;

	private String drugDescription;

	private Double drugQuantity;

	private float drugPrice;

	private String drugId;

	private Integer poId;

	private Integer distributerId;
	
	private Date expireDate;
	
	private String location;

	public StockDrugDTO(PoDrug data) {
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
