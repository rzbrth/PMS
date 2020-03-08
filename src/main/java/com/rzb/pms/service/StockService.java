package com.rzb.pms.service;

import static io.github.perplexhub.rsql.RSQLQueryDslSupport.toPredicate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.rzb.pms.dto.AuditType;
import com.rzb.pms.dto.ExpiredItemReturnReq;
import com.rzb.pms.dto.ExpiredItemReturnWrapper;
import com.rzb.pms.dto.PoDrugDTO;
import com.rzb.pms.dto.PurchaseOrderResponse;
import com.rzb.pms.dto.ReferenceType;
import com.rzb.pms.dto.ReportCategory;
import com.rzb.pms.dto.RequestStatus;
import com.rzb.pms.dto.StockDirectRequestDTO;
import com.rzb.pms.dto.StockDirectRequestDTOWrapper;
import com.rzb.pms.dto.StockResponseDto;
import com.rzb.pms.dto.StockType;
import com.rzb.pms.dto.TopDrugAboutToExpire;
import com.rzb.pms.exception.CustomEntityNotFoundException;
import com.rzb.pms.exception.CustomException;
import com.rzb.pms.model.Audit;
import com.rzb.pms.model.Drug;
import com.rzb.pms.model.ExpiredItemReturn;
import com.rzb.pms.model.QStock;
import com.rzb.pms.model.Stock;
import com.rzb.pms.repository.AuditRepository;
import com.rzb.pms.repository.DistributerRepository;
import com.rzb.pms.repository.DrugRepository;
import com.rzb.pms.repository.ExpiredItemReturnRepository;
import com.rzb.pms.repository.StockRepository;
import com.rzb.pms.utils.BaseUtil;
import com.rzb.pms.utils.ReportUtills;

import lombok.extern.slf4j.Slf4j;

@Service
@SuppressWarnings(value = { "unchecked", "unused", "rawtypes" })
@Slf4j
public class StockService<K> {

	@Autowired
	private StockRepository repository;

	@Autowired
	private DistributerRepository distRepository;

	@Autowired
	private DrugRepository drugRepository;

	@Autowired
	private AuditRepository auditRepo;

	@Autowired
	private ExpiredItemReturnRepository returnRepo;

	private ReportUtills report = new ReportUtills();

	public String addStockWithoutPR(StockDirectRequestDTOWrapper item) {

		String invoiceRefNum = null;
		if (item == null) {
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
						.stockCreatedAt(LocalDate.now()).unitPrice(lineItem.getMrp() / lineItem.getPacking())
						.drugId(lineItem.getDrugId()).invoiceReference(invoiceRefNum)
						.stockType(StockType.DIRECT.toString()).distributerId(lineItem.getDistributerId())
						.drugName(drugRepository.findById(lineItem.getDrugId()).get().getBrandName()).build());
				try {
					auditRepo.save(Audit.builder().auditType(AuditType.STOCK_IN_DIRECT.toString()).createdBy("")
							.createdDate(LocalDate.now()).drugId(lineItem.getDrugId()).build());
				} catch (Exception e) {
					throw new CustomException("Problem while auditing direct stock creation", e);
				}
			}
			return "Stock created Successfully";
		} catch (Exception e) {
			throw new CustomException("problem occured while creating stock", e);
		}
	}

	public String addStockFromPR(PurchaseOrderResponse po) {

		if (po == null) {
			log.error("Stock data can't be empty");
			throw new CustomException("Stock data can't be empty", HttpStatus.BAD_REQUEST);
		}
		try {

			// check if any stock available against the poId
			List<Stock> s = repository.findByPoId(po.getPoId());

			if (!s.isEmpty()) {
				throw new CustomException("Stock for the po(" + "PO_ID=" + po.getPoId() + ") already Created by: "
						+ po.getCreatedBy() + " On " + po.getCreatedDate(), HttpStatus.BAD_REQUEST);
			} else {
				if (po.getPoStatus().equalsIgnoreCase(RequestStatus.PROCESSED.toString())) {
					for (PoDrugDTO stock : po.getPoLineItem()) {

						Drug drugData = drugRepository.findById(stock.getDrugId()).get();

						repository.save(Stock.builder().avlQntyWhole(stock.getDrugQuantity())
								.avlQntyTrimmed(drugData.getPacking() * stock.getDrugQuantity()).createddBy("")
								.stockCreatedAt(LocalDate.now()).expiryDate(LocalDate.now())
								// .genericId(drugData.getGenericId())
								.location(stock.getLocation()).mrp(stock.getDrugPrice()).packing(drugData.getPacking())
								.stockCreatedAt(LocalDate.now()).unitPrice(stock.getDrugPrice() / drugData.getPacking())
								.drugId(stock.getDrugId()).stockType(StockType.FROM_PO.toString())
								.distributerId(stock.getDistributerId()).poId(po.getPoId())
								.invoiceReference(po.getReferenceNumber()).drugName(drugData.getBrandName()).build());
						try {
							auditRepo.save(Audit.builder().auditType(AuditType.STOCK_IN_FROM_PO.toString())
									.createdBy("").createdDate(LocalDate.now()).drugId(drugData.getDrugId()).build());
						} catch (Exception e) {
							throw new CustomException("Problem while auditing stock creation from po", e);
						}
					}
				} else {
					return "Stock can't be created from Pending order";

				}

				return "Stock created Successfully";
			}

		} catch (Exception e) {
			log.error("problem occured while creating stock", e);
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
				.expireDate(r.getExpiryDate()).invoiceReference(r.getInvoiceReference()).location(r.getLocation())
				.mrp(r.getMrp()).packing(r.getPacking()).stockCreatedAt(r.getStockCreatedAt()).stockId(r.getStockId())
				.stockType(r.getStockType()).stockUpdatedAt(r.getStockUpdatedAt()).unitPrice(r.getUnitPrice())
				.updatedBy(r.getUpdatedBy()).expireStatus(BaseUtil.getExpireStatus(r.getExpiryDate()))
				.expireTimeLeft(BaseUtil.remainingExpireTime(r.getExpiryDate())).build();

	}

	public List<K> findAllStock(String filter, PageRequest pageRequest, Boolean isExported, String exportType,
			HttpServletResponse response) {

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

	public StockResponseDto findStockById(Integer stockId) {

		if (stockId == null) {

			throw new CustomException("Stock Id can not be null or empty", HttpStatus.BAD_REQUEST);
		}
		Stock r = repository.findById(stockId).get();

		if (r == null) {
			throw new CustomEntityNotFoundException(Stock.class, "StockId", stockId.toString());
		}

		return StockResponseDto.builder().avlQntyTrimmed(r.getAvlQntyTrimmed()).avlQntyWhole(r.getAvlQntyWhole())
				.createddBy(r.getCreateddBy()).distributerId(r.getDistributerId()).drugId(r.getDrugId())
				.expireDate(r.getExpiryDate()).invoiceReference(r.getInvoiceReference()).location(r.getLocation())
				.mrp(r.getMrp()).packing(r.getPacking()).stockCreatedAt(r.getStockCreatedAt()).stockId(r.getStockId())
				.stockType(r.getStockType()).stockUpdatedAt(r.getStockUpdatedAt()).unitPrice(r.getUnitPrice())
				.updatedBy(r.getUpdatedBy()).expireStatus(BaseUtil.getExpireStatus(r.getExpiryDate()))
				.drugName(r.getDrugName()).expireTimeLeft(BaseUtil.remainingExpireTime(r.getExpiryDate())).build();

	}

	public List<TopDrugAboutToExpire> checkForAboutToExpireItem() {

		return repository.findTopDrugAboutToExpire().stream().map(x -> new TopDrugAboutToExpire(x))
				.collect(Collectors.toList());
	}

	@Transactional
	public String updateStock() {

		return null;

	}

	@Transactional
	public String emptyExpiredStock(ExpiredItemReturnWrapper wrapper) {

		if (wrapper == null) {
			throw new CustomException("Expired Stock return request data can't be empty", HttpStatus.BAD_REQUEST);
		}

		for (ExpiredItemReturnReq re : wrapper.getRe()) {
			try {
				ExpiredItemReturn data = ExpiredItemReturn.builder().distributerId(re.getDistributerId())
						.drugId(re.getDrugId()).drugName(re.getDrugName()).expiryDate(re.getExpiryDate())
						.invoiceReference(re.getInvoiceReference()).location(re.getLocation()).mrp(re.getMrp())
						.packing(re.getPacking()).poId(re.getPoId()).stockId(re.getStockId())
						.status(RequestStatus.PENDING.toString()).unitPrice(re.getUnitPrice()).build();

				returnRepo.saveAndFlush(data);

				repository.deleteById(re.getStockId());

				auditRepo.save(Audit.builder().auditType(AuditType.EXPIRED_STOCK_RETURN.toString())
						.drugId(re.getDrugId()).poId(re.getPoId()).returnId(data.getReturnId()).stockId(re.getStockId())
						.createdBy("").createdDate(LocalDate.now()).build());
			} catch (Exception e) {
				throw new CustomException(
						"Problem while creating return request for expired item:- StockId = " + re.getStockId(), e);
			}

		}

		return "Return request created successfully";

	}

	public String deleteStockById(Integer stockId) {

		if (stockId == null || stockId == 0) {
			throw new CustomException("Stock Id can't be null or zeo", HttpStatus.BAD_REQUEST);
		}
		Stock stock = repository.findById(stockId).get();
		if (stock == null) {
			throw new CustomEntityNotFoundException(Stock.class, "Stock Id", stockId.toString());
		}
		try {
			repository.deleteById(stockId);
			auditRepo.save(Audit.builder().auditType(AuditType.STOCK_DELETED.toString()).drugId(stock.getDrugId())
					.poId(stock.getPoId()).updatedBy("").updatedDate(LocalDate.now()).build());
		} catch (Exception e) {
			throw new CustomException("Problem while deleting stock:--" + "Stock Id = " + stockId, e);
		}

		return "Stock Deleted Successfully";
	}

}
