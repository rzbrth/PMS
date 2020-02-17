package com.rzb.pms.service;

import static io.github.perplexhub.rsql.RSQLQueryDslSupport.toPredicate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.rzb.pms.dto.AuditType;
import com.rzb.pms.dto.EntityInfoRequest;
import com.rzb.pms.dto.OrderStatus;
import com.rzb.pms.dto.PoDrugDTO;
import com.rzb.pms.dto.PurchaseOrderDTO;
import com.rzb.pms.dto.PurchaseOrderResponse;
import com.rzb.pms.dto.ReferenceType;
import com.rzb.pms.dto.ReportCategory;
import com.rzb.pms.dto.StockDirectRequestDTO;
import com.rzb.pms.dto.StockDirectRequestDTOWrapper;
import com.rzb.pms.dto.StockResponseDto;
import com.rzb.pms.dto.StockType;
import com.rzb.pms.dto.TopDrugAboutToExpire;
import com.rzb.pms.exception.CustomEntityNotFoundException;
import com.rzb.pms.exception.CustomException;
import com.rzb.pms.log.Log;
import com.rzb.pms.model.Audit;
import com.rzb.pms.model.Drug;
import com.rzb.pms.model.QStock;
import com.rzb.pms.model.Stock;
import com.rzb.pms.repository.AuditRepository;
import com.rzb.pms.repository.DistributerRepository;
import com.rzb.pms.repository.DrugRepository;
import com.rzb.pms.repository.StockRepository;
import com.rzb.pms.utils.BaseUtil;
import com.rzb.pms.utils.ReportUtills;

@Service
@SuppressWarnings("unchecked")
public class StockService<K> {

	@Log
	private Logger logger;

	@Autowired
	private StockRepository repository;

	@Autowired
	private DistributerRepository distRepository;

	@Autowired
	private DrugRepository drugRepository;

	@Autowired
	private AuditRepository auditRepo;

	private ReportUtills report = new ReportUtills();

	public String addStockWithoutPR(StockDirectRequestDTOWrapper item) {

		String invoiceRefNum = null;
		if (item == null) {
			logger.error("Stock data can't be empty");
			throw new CustomException("Stock data can't be empty", HttpStatus.BAD_REQUEST);
		}
		try {

			for (StockDirectRequestDTO lineItem : item.getData()) {

				if (item.getPurchaseInvoiceNumber() == null) {
					invoiceRefNum = BaseUtil.getRandomReference(ReferenceType.STOCK.toString());
				} else {
					invoiceRefNum = item.getPurchaseInvoiceNumber();
				}
				repository.save(Stock.builder().avlQntyWhole(lineItem.getAvlQntyWhole())
						.avlQntyTrimmed(lineItem.getPacking() * lineItem.getAvlQntyWhole()).createddBy("")
						.expiryDate(lineItem.getExpiryDate())
						// .genericId(lineItem.getGenericId())
						.location(lineItem.getLocation()).mrp(lineItem.getMrp()).packing(lineItem.getPacking())
						.stockCreatedAt(new Date()).unitPrice(lineItem.getMrp() / lineItem.getPacking())
						.drugId(lineItem.getDrugId()).invoiceReference(invoiceRefNum)
						.stockType(StockType.DIRECT.toString()).distributerId(lineItem.getDistributerId())
						.drugName(drugRepository.findById(lineItem.getDrugId()).get().getBrandName()).build());
				try {
					auditRepo.save(Audit.builder().auditType(AuditType.STOCK_IN_DIRECT.toString()).createdBy("")
							.createdDate(new Date()).drugId(lineItem.getDrugId()).build());
				} catch (Exception e) {
					throw new CustomException("Problem while auditing direct stock creation", e);
				}
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
								// .genericId(drugData.getGenericId())
								.location(stock.getLocation()).mrp(stock.getDrugPrice()).packing(drugData.getPacking())
								.stockCreatedAt(new Date()).unitPrice(stock.getDrugPrice() / drugData.getPacking())
								.drugId(stock.getDrugId()).stockType(StockType.FROM_PO.toString())
								.distributerId(stock.getDistributerId()).poId(data.getPoId())
								.invoiceReference(data.getReferenceNumber()).drugName(drugData.getBrandName()).build());
						try {
							auditRepo.save(Audit.builder().auditType(AuditType.STOCK_IN_FROM_PO.toString())
									.createdBy("").createdDate(new Date()).drugId(drugData.getDrugId()).build());
						} catch (Exception e) {
							throw new CustomException("Problem while auditing stock creation from po", e);
						}
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
				.expireDate(DateFormatUtils.format(r.getExpiryDate(), "dd-MM-yyyy HH:mm:SS"))
				.invoiceReference(r.getInvoiceReference()).location(r.getLocation()).mrp(r.getMrp())
				.packing(r.getPacking()).stockCreatedAt(r.getStockCreatedAt()).stockId(r.getStockId())
				.stockType(r.getStockType()).stockUpdatedAt(r.getStockUpdatedAt()).unitPrice(r.getUnitPrice())
				.updatedBy(r.getUpdatedBy()).expireStatus(BaseUtil.getExpireStatus(r.getExpiryDate()))
				.expireTimeLeft(BaseUtil.remainingExpireTime(r.getExpiryDate())).build();

	}

	public List<K> findStockDetails(String filter, PageRequest pageRequest, Boolean isExported, String exportType,
			Integer stockId, HttpServletResponse response, String entityInfoRequest) {

		List<Stock> stockInfo = new ArrayList<Stock>();
		List<StockResponseDto> expo = new ArrayList<StockResponseDto>();

		if (isExported) {

			try {
				if (filter == null || filter.isEmpty()) {
					stockInfo = repository.findAll(pageRequest).getContent();
				} else {
					stockInfo = repository.findAll(toPredicate(filter, QStock.stock), pageRequest).getContent();
				}
				List<K> data = (List<K>) report.generateReport(response, exportType,
						ReportCategory.ALL_STOCK.toString(),
						stockInfo.stream().map(x -> new StockResponseDto(x)).collect(Collectors.toList()));
				return data;
			} catch (Exception e) {
				throw new CustomException("Problem while exporting stock info to :--<< (" + exportType
						+ ")-->> \"+\"\\t\"+\"exception occured-->", e);
			}
		}

		if (entityInfoRequest.equalsIgnoreCase(EntityInfoRequest.FIND_ONE.toString())) {

			Stock r = repository.findById(stockId).get();
			if (r == null) {
				throw new CustomEntityNotFoundException(Stock.class, "StockId", stockId.toString());
			}

			expo.add(StockResponseDto.builder().avlQntyTrimmed(r.getAvlQntyTrimmed()).avlQntyWhole(r.getAvlQntyWhole())
					.createddBy(r.getCreateddBy()).distributerId(r.getDistributerId()).drugId(r.getDrugId())
					.expireDate(DateFormatUtils.format(r.getExpiryDate(), "dd-MM-yyyy HH:mm:SS"))
					.invoiceReference(r.getInvoiceReference()).location(r.getLocation()).mrp(r.getMrp())
					.packing(r.getPacking()).stockCreatedAt(r.getStockCreatedAt()).stockId(r.getStockId())
					.stockType(r.getStockType()).stockUpdatedAt(r.getStockUpdatedAt()).unitPrice(r.getUnitPrice())
					.updatedBy(r.getUpdatedBy()).expireStatus(BaseUtil.getExpireStatus(r.getExpiryDate()))
					.drugName(r.getDrugName()).expireTimeLeft(BaseUtil.remainingExpireTime(r.getExpiryDate())).build());

			return (List<K>) expo;
		} else {
			if (filter == null || filter.isEmpty()) {
				stockInfo = repository.findAll(pageRequest).getContent();
			} else {
				stockInfo = repository.findAll(toPredicate(filter, QStock.stock), pageRequest).getContent();
			}
			if (stockInfo.isEmpty()) {
				throw new CustomException("No Stock available", HttpStatus.NOT_FOUND);
			}
			return (List<K>) stockInfo.stream().map(x -> new StockResponseDto(x)).collect(Collectors.toList());
		}

	}

	public List<TopDrugAboutToExpire> checkForExpiry() {

		return repository.findTopDrugAboutToExpire().stream().map(x -> new TopDrugAboutToExpire(x))
				.collect(Collectors.toList());
	}

	@Transactional
	public String updateStock() {

		return null;

	}

	public String emptyExpiredStock() {
		return null;

	}

}
