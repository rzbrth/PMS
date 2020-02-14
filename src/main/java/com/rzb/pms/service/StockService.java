package com.rzb.pms.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.rzb.pms.dto.OrderStatus;
import com.rzb.pms.dto.PoDrugDTO;
import com.rzb.pms.dto.PurchaseOrderDTO;
import com.rzb.pms.dto.PurchaseOrderResponse;
import com.rzb.pms.dto.ReportCategory;
import com.rzb.pms.dto.StockDirectRequestDTO;
import com.rzb.pms.dto.StockDirectRequestDTOWrapper;
import com.rzb.pms.dto.StockResponseDto;
import com.rzb.pms.dto.StockType;
import com.rzb.pms.exception.CustomEntityNotFoundException;
import com.rzb.pms.exception.CustomException;
import com.rzb.pms.log.Log;
import com.rzb.pms.model.Drug;
import com.rzb.pms.model.Stock;
import com.rzb.pms.repository.DrugRepository;
import com.rzb.pms.repository.StockRepository;
import com.rzb.pms.utils.ReportUtills;

@Service
@SuppressWarnings("unchecked")
public class StockService<K> {

	@Log
	private Logger logger;

	@Autowired
	private StockRepository repository;

	@Autowired
	private DrugRepository drugRepository;
	
	private ReportUtills report = new ReportUtills();


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

	public String addStockFromPR(PurchaseOrderResponse po) {

		if (po == null) {
			logger.error("Stock data can't be empty");
			throw new CustomException("Stock data can't be empty", HttpStatus.BAD_REQUEST);
		}
		try {

			for (PurchaseOrderDTO data : po.getPoData()) {

				if (data.getPoStatus().equalsIgnoreCase(OrderStatus.PROCESSED.toString())) {
					for (PoDrugDTO stock : data.getPoLineItem()) {

						Drug drugData = drugRepository.findById(stock.getDrugId()).get();

						repository.save(Stock.builder().avlQntyWhole(stock.getDrugQuantity())
								.avlQntyTrimmed(drugData.getPacking() * stock.getDrugQuantity()).createddBy("")
								.stockCreatedAt(new Date()).expiryDate(stock.getExpireDate())
								.genericId(drugData.getGenericId()).location(stock.getLocation())
								.mrp(stock.getDrugPrice()).packing(drugData.getPacking()).stockCreatedAt(new Date())
								.unitPrice(stock.getDrugPrice() / drugData.getPacking()).drugId(stock.getDrugId())
								.stockType(StockType.FROM_PO.toString()).distributerId(stock.getDistributerId())
								.poId(data.getPoId()).build());

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

	public StockResponseDto getStockById(Integer stockId) {

		if (stockId == null) {
			throw new CustomException("Stock Id can't be empty", HttpStatus.BAD_REQUEST);
		}
		Stock r = repository.findById(stockId).get();
		if (r == null) {
			throw new CustomEntityNotFoundException(Stock.class, "StockId", stockId.toString());
		}

		return StockResponseDto.builder().avlQntyTrimmed(r.getAvlQntyTrimmed()).avlQntyWhole(r.getAvlQntyWhole())
				.createddBy(r.getCreateddBy()).distributerId(r.getDistributerId()).drugId(r.getDrugId())
				.expiryDate(r.getExpiryDate()).invoiceReference(r.getInvoiceReference()).location(r.getLocation())
				.mrp(r.getMrp()).packing(r.getPacking()).poReferenseNumber(r.getPoReferenseNumber())
				.stockCreatedAt(r.getStockCreatedAt()).stockId(r.getStockId()).stockType(r.getStockType())
				.stockUpdatedAt(r.getStockUpdatedAt()).unitPrice(r.getUnitPrice()).updatedBy(r.getUpdatedBy()).build();

	}
	
	
	public List<K> findAllStocks(Pageable pageable, Boolean isExported, String exportType,
			HttpServletResponse response) {

		Page<Stock> stockData = repository.findAll(pageable);

		if (stockData.isEmpty()) {
			throw new CustomException("No Stock available", HttpStatus.NOT_FOUND);
		}

		if (isExported) {

			try {
				List<K> data = (List<K>) report.generateReport(response, exportType, ReportCategory.ALL_STOCK.toString(),
						stockData.getContent().stream().map(x -> new StockResponseDto(x)).collect(Collectors.toList()));
				return data;
			} catch (Exception e) {
				throw new CustomException("Problem while exporting stock info to :--<< (" + exportType + ")-->> \"+\"\\t\"+\"exception occured-->", e);
			}
		}

		return (List<K>) stockData.getContent().stream().map(x -> new StockResponseDto(x)).collect(Collectors.toList());
	}

	@Transactional
	public String updateStock() {

		
		
		
		
		return null;

	}

	public String emptyExpiredStock() {
		return null;

	}

}
