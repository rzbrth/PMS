package com.rzb.pms.service;

import static io.github.perplexhub.rsql.RSQLQueryDslSupport.toPredicate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.google.common.base.Strings;
import com.rzb.pms.dto.ExpiredItemReturnReq;
import com.rzb.pms.dto.ExpiredItemReturnWrapper;
import com.rzb.pms.dto.PoDrugDTO;
import com.rzb.pms.dto.PurchaseOrderResponse;
import com.rzb.pms.dto.StockDirectRequestDTO;
import com.rzb.pms.dto.StockDirectRequestDTOWrapper;
import com.rzb.pms.dto.StockResponseDto;
import com.rzb.pms.dto.TopDrugAboutToExpire;
import com.rzb.pms.model.Audit;
import com.rzb.pms.model.Drug;
import com.rzb.pms.model.ExpiredItemReturn;
import com.rzb.pms.model.QStock;
import com.rzb.pms.model.Stock;
import com.rzb.pms.model.enums.AuditType;
import com.rzb.pms.model.enums.ReferenceType;
import com.rzb.pms.model.enums.ReportCategory;
import com.rzb.pms.model.enums.RequestStatus;
import com.rzb.pms.model.enums.StockType;
import com.rzb.pms.projection.StockProjection;
import com.rzb.pms.repository.AuditRepository;
import com.rzb.pms.repository.DistributerRepository;
import com.rzb.pms.repository.DrugRepository;
import com.rzb.pms.repository.ExpiredItemReturnRepository;
import com.rzb.pms.repository.StockRepository;
import com.rzb.pms.rsql.SearchCriteria;
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

	/*
	 * Create stock directly
	 */
	@Transactional
	public String addStockWithoutPR(StockDirectRequestDTOWrapper item) {

		String invoiceRefNum = null;
		if (item == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock data can't be empty");
		}
		try {

			for (StockDirectRequestDTO lineItem : item.getData()) {

				if (item.getPurchaseInvoiceNumber() == null) {
					invoiceRefNum = BaseUtil.getRandomReference(ReferenceType.STOCK.toString());
				} else {
					invoiceRefNum = item.getPurchaseInvoiceNumber();
				}
				repository.save(Stock.builder().avlQntyWhole(lineItem.getAvlQntyWhole())
						.avlQntyTrimmed(lineItem.getPacking() * lineItem.getAvlQntyWhole())
						.createddBy(BaseUtil.getLoggedInuserName()).expiryDate(lineItem.getExpiryDate())
						// .genericId(lineItem.getGenericId())
						.location(lineItem.getLocation()).mrp(lineItem.getMrp()).packing(lineItem.getPacking())
						.stockCreatedAt(LocalDate.now()).unitPrice(lineItem.getMrp() / lineItem.getPacking())
						.drugId(lineItem.getDrugId()).invoiceReference(invoiceRefNum)
						.stockType(StockType.DIRECT.toString()).distributerId(lineItem.getDistributerId())
						.drugName(drugRepository.findById(lineItem.getDrugId()).orElse(null).getBrandName()).build());
				// Auditing
				try {
					auditRepo.save(Audit.builder().auditType(AuditType.STOCK_IN_DIRECT.toString())
							.createdBy(BaseUtil.getLoggedInuserName()).createdDate(LocalDate.now())
							.drugId(lineItem.getDrugId()).build());
				} catch (Exception e) {
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
							"Problem while auditing direct stock creation", e);
				}
			}
			return "Stock created Successfully";
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "problem occured while creating stock",
					e);
		}
	}

	/*
	 * Create stock from existing purchase order
	 */
	@Transactional
	public String addStockFromPR(PurchaseOrderResponse po) {

		if (po == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock data can't be empty");
		}
		try {

			// check if any stock available against the poId
			List<Stock> s = repository.findByPoId(po.getPoId());

			if (!s.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock for the po(" + "PO_ID=" + po.getPoId()
						+ ") already Created by: " + po.getCreatedBy() + " On " + po.getCreatedDate());
			} else {
				if (po.getPoStatus().equalsIgnoreCase(RequestStatus.PROCESSED.toString())) {
					for (PoDrugDTO stock : po.getPoLineItem()) {

						Drug drugData = drugRepository.findById(stock.getDrugId()).orElse(null);
						if (drugData == null) {
							throw new ResponseStatusException(HttpStatus.NO_CONTENT,
									"No stock aavailable for drug id : " + stock.getDrugId());
						}

						repository.save(Stock.builder().avlQntyWhole(stock.getDrugQuantity())
								.avlQntyTrimmed(drugData.getPacking() * stock.getDrugQuantity())
								.createddBy(BaseUtil.getLoggedInuserName()).stockCreatedAt(LocalDate.now())
								.expiryDate(LocalDate.now()).poLId(stock.getPoDrugId())
								// .genericId(drugData.getGenericId())
								.location(stock.getLocation()).mrp(stock.getDrugPrice()).packing(drugData.getPacking())
								.stockCreatedAt(LocalDate.now()).unitPrice(stock.getDrugPrice() / drugData.getPacking())
								.drugId(stock.getDrugId()).stockType(StockType.FROM_PO.toString())
								.distributerId(stock.getDistributerId()).poId(po.getPoId())
								.invoiceReference(po.getReferenceNumber()).drugName(drugData.getBrandName()).build());
						// Auditing
						try {
							auditRepo.save(Audit.builder().auditType(AuditType.STOCK_IN_FROM_PO.toString())
									.createdBy(BaseUtil.getLoggedInuserName()).createdDate(LocalDate.now())
									.drugId(drugData.getDrugId()).build());
						} catch (Exception e) {
							throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
									"Problem while auditing stock creation from po", e);
						}
					}
				} else {
					return "Stock can't be created from Pending order";

				}

				return "Stock created Successfully";
			}

		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "problem occured while creating stock",
					e);
		}
	}

	/*
	 * Find individual stock info by id
	 */
	public StockResponseDto getStockById(Integer stockId) {

		if (stockId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock Id can't be empty");
		}
		Stock r = repository.findById(stockId).orElse(null);
		if (r == null) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No stock found for id : " + stockId);
		}

		return StockResponseDto.builder().avlQntyTrimmed(r.getAvlQntyTrimmed()).avlQntyWhole(r.getAvlQntyWhole())
				.createddBy(r.getCreateddBy()).distributerId(r.getDistributerId()).drugId(r.getDrugId())
				.expireDate(r.getExpiryDate()).invoiceReference(r.getInvoiceReference()).location(r.getLocation())
				.mrp(r.getMrp()).packing(r.getPacking()).stockCreatedAt(r.getStockCreatedAt()).stockId(r.getStockId())
				.stockType(r.getStockType()).stockUpdatedAt(r.getStockUpdatedAt()).unitPrice(r.getUnitPrice())
				.updatedBy(r.getUpdatedBy()).expireStatus(BaseUtil.getExpireStatus(r.getExpiryDate()))
				.expireTimeLeft(BaseUtil.remainingExpireTime(r.getExpiryDate())).build();

	}

	/*
	 * Find all stock It supports various searching and sorting criteria
	 * stockCreatedAt, stockUpdatedAt, expiryDate only supports =bt=,=nb=
	 */
	public List<K> findAllStock(String filter, PageRequest pageRequest, Boolean isExported, String exportType,
			HttpServletResponse response) {

		List<Stock> stockInfo = new ArrayList<Stock>();
		List<StockResponseDto> expo = new ArrayList<StockResponseDto>();
		List<String> parsedSearch = new ArrayList<String>();
		if (filter != null) {
			String[] queryParams = filter.split(";");
			for (String queryParam : queryParams) {
				parsedSearch.add(BaseUtil.pareseIncommingSearchRequest(queryParam));
			}
		}

		// Remove ; from last element
		// parsedSearch.set(parsedSearch.size() - 1,
		// parsedSearch.get(parsedSearch.size() - 1).replace(";", ""));
		// filter = parsedSearch.
		filter = String.join(";", parsedSearch);

		if (isExported) {

			try {
				if (Strings.isNullOrEmpty(filter)) {
					stockInfo = repository.findAll(pageRequest).getContent();
				} else {
					stockInfo = repository.findAll(toPredicate(filter, QStock.stock), pageRequest).getContent();
				}
				List<K> data = (List<K>) report.generateReport(response, exportType,
						ReportCategory.ALL_STOCK.toString(),
						stockInfo.stream().map(x -> new StockResponseDto(x)).collect(Collectors.toList()));
				return data;
			} catch (Exception e) {

				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Problem while exporting stock info to :--<< (" + exportType
								+ ")-->> \"+\"\\t\"+\"exception occured-->",
						e);
			}
		}

		if (Strings.isNullOrEmpty(filter)) {
			stockInfo = repository.findAll(pageRequest).getContent();
		} else {
			stockInfo = repository.findAll(toPredicate(filter, QStock.stock), pageRequest).getContent();
		}
		if (stockInfo.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No Stock available");
		}
		return (List<K>) stockInfo.stream().map(x -> new StockResponseDto(x)).collect(Collectors.toList());

	}

	public StockResponseDto findStockById(Integer stockId) {

		if (stockId == null) {

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock Id can not be null or empty");
		}
		Stock r = repository.findById(stockId).orElse(null);
		if (r == null) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No stock found for id : " + stockId);
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

		List<StockProjection> data = repository.findTopDrugAboutToExpire();
		if (data.isEmpty()) {
			log.debug("No expired drug information found");
			return Collections.EMPTY_LIST;
		}
		for (StockProjection s : data) {
			log.debug("Id, Name, ExpDate: {},{},{}", s.getDrugName(), s.getExpiryDate());
		}
		return data.stream().map(x -> new TopDrugAboutToExpire(x)).collect(Collectors.toList());
	}

	@Transactional
	public String updateStock() {

		return null;

	}

	@Transactional
	public String emptyExpiredStock(ExpiredItemReturnWrapper wrapper) {

		if (wrapper == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Expired Stock return request data can't be empty");
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
						.createdBy(BaseUtil.getLoggedInuserName()).createdDate(LocalDate.now()).build());
			} catch (Exception e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Problem while creating return request for expired item:- StockId = " + re.getStockId(), e);
			}

		}

		return "Return request created successfully";

	}

	@Transactional
	public String deleteStockById(Integer stockId) {

		if (stockId == null || stockId == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock Id can't be null or zeo");
		}
		Stock stock = repository.findById(stockId).orElse(null);
		if (stock == null) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No stock found for id : " + stockId);
		}
		try {
			repository.deleteById(stockId);
			// Auditing
			auditRepo.save(Audit.builder().auditType(AuditType.STOCK_DELETED.toString()).drugId(stock.getDrugId())
					.poId(stock.getPoId()).updatedBy(BaseUtil.getLoggedInuserName()).updatedDate(LocalDate.now())
					.build());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception occured", e);
		}

		return "Stock Deleted Successfully";
	}

}
