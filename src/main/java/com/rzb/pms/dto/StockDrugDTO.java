package com.rzb.pms.dto;

import java.util.Date;

import com.rzb.pms.model.PurchaseOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockDrugDTO {

	private Integer poId;

	private String drugName;

	private String drugDescription;

	private Double drugQuantity;

	private float drugPrice;

	private String drugId;

	private Integer poLId;

	private Integer distributerId;

	private Date expireDate;

	private String location;

	public StockDrugDTO(PurchaseOrder data) {
		this.distributerId = data.getDistributerId();
		this.drugDescription = data.getDrugDescription();
		this.drugId = data.getDrugId();
		this.drugName = data.getDrugName();
		this.drugPrice = data.getDrugPrice();
		this.drugQuantity = data.getDrugQuantity();
		this.poId = data.getPoId();
		this.poLId = data.getPoLId();
	}
}
