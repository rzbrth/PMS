package com.rzb.pms.dto;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rzb.pms.model.Stock;
import com.rzb.pms.repository.DistributerRepository;
import com.rzb.pms.repository.DrugRepository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DrugAboutToExpireStatus {

	private String drugId;

	private String drugName;

	private Integer stockId;

	private Double avlQuantityInWhole;

	private Double avlQuantityInTrimmed;

	private String expireStatus;

	private String expireDate;

	private String expireTimeLeft;

	private String location;

	private String invoiceReference;

	private String distributerName;

	private Integer distributerId;

	public static DrugAboutToExpireStatus buildWithStockInfo(Stock stock, String drugName, String distributerName) {

		String status = null;
		if (stock.getExpiryDate().before(new Date())) {
			status = ExpireStatus.EXPIRED.toString();
		} else if (stock.getExpiryDate().after(new Date())) {
			status = ExpireStatus.ABOUT_TO_EXPIRE.toString();
		}
		Long expireTimeRemains = ChronoUnit.DAYS.between(new Date().toInstant(), stock.getExpiryDate().toInstant());

		return DrugAboutToExpireStatus.builder().avlQuantityInTrimmed(stock.getAvlQntyTrimmed())
				.avlQuantityInWhole(stock.getAvlQntyWhole()).distributerId(stock.getDistributerId())
				.distributerName(distributerName)
				.drugId(stock.getDrugId()).drugName(drugName)
				.expireDate(DateFormatUtils.format(stock.getExpiryDate(), "dd-MM-yyyy HH:mm:SS"))
				.invoiceReference(stock.getInvoiceReference()).location(stock.getLocation()).stockId(stock.getStockId())
				.expireStatus(status).expireTimeLeft(String.valueOf(expireTimeRemains)).build();
	}
}
