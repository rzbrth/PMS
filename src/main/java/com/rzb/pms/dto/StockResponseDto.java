package com.rzb.pms.dto;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.rzb.pms.model.Stock;
import com.rzb.pms.utils.BaseUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockResponseDto {

	private Integer stockId;

	private String drugId;

	private Date stockCreatedAt;

	private Date stockUpdatedAt;

	private String createddBy;

	private String updatedBy;

	private Integer packing;

	private Float mrp;

	private Float unitPrice;

	// private String genericId;

	private String location;

	private String expireStatus;

	private String expireDate;

	private String expireTimeLeft;

	private Double avlQntyWhole;

	private Double avlQntyTrimmed;

	private Integer distributerId;

	private String stockType;

	private String invoiceReference;

	private Integer poId;

	private Integer poLid;

	private String drugName;

	public StockResponseDto(Stock data) {

		this.avlQntyTrimmed = data.getAvlQntyTrimmed();
		this.avlQntyWhole = data.getAvlQntyWhole();
		this.createddBy = data.getCreateddBy();
		this.distributerId = data.getDistributerId();
		this.drugId = data.getDrugId();
		this.invoiceReference = data.getInvoiceReference();
		this.location = data.getLocation();
		this.mrp = data.getMrp();
		this.packing = data.getPacking();
		this.stockCreatedAt = data.getStockCreatedAt();
		this.stockId = data.getStockId();
		this.stockType = data.getStockType();
		this.stockUpdatedAt = data.getStockUpdatedAt();
		this.unitPrice = data.getUnitPrice();
		this.updatedBy = data.getUpdatedBy();
		this.expireDate = DateFormatUtils.format(data.getExpiryDate(), "dd-MM-yyyy HH:mm:SS");
		this.expireStatus = BaseUtil.getExpireStatus(data.getExpiryDate());
		this.expireTimeLeft = BaseUtil.remainingExpireTime(data.getExpiryDate());
		this.drugName = data.getDrugName();
	}
}
