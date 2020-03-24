package com.rzb.pms.dto;

import com.rzb.pms.projection.StockProjection;
import com.rzb.pms.utils.BaseUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopDrugAboutToExpire {

	private String drugName;

	private Integer stockId;

	private String expireTimeLeft;

	public TopDrugAboutToExpire(StockProjection s) {

		this.drugName = s.getDrugName();
		this.stockId = s.getStockId();
		this.expireTimeLeft = BaseUtil.remainingExpireTime(s.getExpiryDate());
	}

}
