package com.rzb.pms.service;

import java.util.Date;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.rzb.pms.dto.OrderStatus;
import com.rzb.pms.dto.PurchaseOrderDTO;
import com.rzb.pms.dto.PurchaseOrderLineItemsDTO;
import com.rzb.pms.dto.PurchaseOrderLineItemResponse;
import com.rzb.pms.dto.StockDirectRequestDTO;
import com.rzb.pms.dto.StockDirectRequestDTOWrapper;
import com.rzb.pms.dto.StockType;
import com.rzb.pms.exception.CustomException;
import com.rzb.pms.log.Log;
import com.rzb.pms.model.Drug;
import com.rzb.pms.model.Stock;
import com.rzb.pms.repository.DrugRepository;
import com.rzb.pms.repository.StockRepository;

@Service
public class StockService {

	@Log
	private Logger logger;

	@Autowired
	private StockRepository repository;

	@Autowired
	private DrugRepository drugRepository;

	public String addStockWithoutPR(StockDirectRequestDTOWrapper item) {

		if (item == null) {
			logger.error("Stock data can't be empty");
			throw new CustomException("Stock data can't be empty", HttpStatus.BAD_REQUEST);
		}
		try {

			for (StockDirectRequestDTO lineItem : item.getData()) {

				repository.save(Stock.builder().avlQntyWhole(lineItem.getAvlQntyWhole())
						.avlQntyTrimmed(lineItem.getPacking() * lineItem.getAvlQntyWhole()).createddBy("")
						.expiryDate(lineItem.getExpiryDate()).genericId(lineItem.getGenericId())
						.location(lineItem.getLocation()).mrp(lineItem.getMrp()).packing(lineItem.getPacking())
						.stockCreatedAt(new Date()).unitPrice(lineItem.getMrp() / lineItem.getPacking())
						.drugId(lineItem.getDrugId()).invoiceReference(item.getPurchaseInvoiceNumber())
						.stockType(StockType.DIRECT.toString()).distributerId(lineItem.getDistributerId()).build());
			}
			return "Stock created Successfully";
		} catch (Exception e) {
			logger.error("problem occured while creating stock", e);
			throw new CustomException("problem occured while creating stock", e);
		}
	}

	public String addStockFromPR(PurchaseOrderLineItemResponse po) {

		if (po == null) {
			logger.error("Stock data can't be empty");
			throw new CustomException("Stock data can't be empty", HttpStatus.BAD_REQUEST);
		}
		try {

			for (PurchaseOrderLineItemsDTO data : po.getPoData()) {

				if (data.getPoStatus().equalsIgnoreCase(OrderStatus.PROCESSED.toString())) {
					for (PurchaseOrderDTO stock : data.getPoLineItem()) {

						Drug drugData = drugRepository.findById(stock.getDrugId()).get();

						repository.save(Stock.builder().avlQntyWhole(stock.getDrugQuantity())
								.avlQntyTrimmed(drugData.getPacking() * stock.getDrugQuantity()).createddBy("")
								.stockCreatedAt(new Date()).expiryDate(stock.getExpireDate())
								.genericId(drugData.getGenericId()).location(stock.getLocation())
								.mrp(stock.getDrugPrice()).packing(drugData.getPacking()).stockCreatedAt(new Date())
								.unitPrice(stock.getDrugPrice() / drugData.getPacking()).drugId(stock.getDrugId())
								.stockType(StockType.FROM_PO.toString()).distributerId(stock.getDistributerId())
								.invoiceReference(po.getPurchaseInvoiceNumber()).poLId(data.getPoLId())
								.poReferenseNumber(data.getPoReference()).build());

					}
				} else {
					return "Stock can't be created from Pending order";

				}

			}

			return "Stock created Successfully";
		} catch (Exception e) {
			logger.error("problem occured while creating stock", e);
			throw new CustomException("problem occured while creating stock", e);
		}
	}
	
	
	
	@Transactional
	public String updateStock() {
		
		
		
		
		return null;
		
	}
	
	public String emptyExpiredStock() {
		return null;
		
	}

}
