package com.rzb.pms.service;

import static io.github.perplexhub.rsql.RSQLQueryDslSupport.toPredicate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.rzb.pms.dto.AddToCartDTOReq;
import com.rzb.pms.dto.AddToCartDTORes;
import com.rzb.pms.dto.AddToCartWrapperReq;
import com.rzb.pms.dto.AddToCartWrapperRes;
import com.rzb.pms.dto.DispenseLineItemsDTO;
import com.rzb.pms.dto.DispenseResponseDTO;
import com.rzb.pms.dto.DrugDispenseWrapperDTO;
import com.rzb.pms.model.Audit;
import com.rzb.pms.model.Customer;
import com.rzb.pms.model.Dispense;
import com.rzb.pms.model.DispenseLineItems;
import com.rzb.pms.model.Drug;
import com.rzb.pms.model.QDispense;
import com.rzb.pms.model.Stock;
import com.rzb.pms.model.enums.AuditType;
import com.rzb.pms.model.enums.DrugType;
import com.rzb.pms.model.enums.ReferenceType;
import com.rzb.pms.model.enums.StockType;
import com.rzb.pms.repository.AuditRepository;
import com.rzb.pms.repository.CustomerRepository;
import com.rzb.pms.repository.DispenseRepository;
import com.rzb.pms.repository.DrugDispenseLineItemRepository;
import com.rzb.pms.repository.DrugRepository;
import com.rzb.pms.repository.StockRepository;
import com.rzb.pms.utils.BaseUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DrugDispensingService {

	@Autowired
	private DrugDispenseLineItemRepository dispenseLineItemRepository;

	@Autowired
	private DispenseRepository dispenseRepo;

	@Autowired
	private DrugRepository repository;

	@Autowired
	private AuditRepository auditRepository;

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private DocumentService documentService;

	private static final String PRINT = "PRINT";
	private static final String EMAIL = "EMAIL";

	/*
	 * This will calculate price based on tax and discount and will add items to
	 * cart
	 */
	public AddToCartWrapperRes addToCard(AddToCartWrapperReq wrapper) {

		// OtherInfoDTO restData = wrapper.getInfo();
		List<AddToCartDTORes> lineItem = new ArrayList<AddToCartDTORes>();
		for (AddToCartDTOReq item : wrapper.getItem()) {

			if (item == null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No drug selected for dispense");

			}
			Double reqQntyInTrimmed = null, avlQntyInTrimmed = null, avlQntyInWhole = null, reqQntyInWhole = null;

			float itemSellPriceBeforeDiscount = 0, itemSellPriceAfterDiscount = 0, itemSellPriceAfterTax = 0,
					discount = 0;

			Drug drugData = repository.findById(item.getDrugId()).orElse(null);
			if (drugData == null) {
				throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No drug found for given id");
			}

			Stock stock = null;
			if (item.getDrugUnit().equalsIgnoreCase(DrugType.TRIMMED.toString())) {
				stock = stockRepository.findStockWithTrimmedQnty(item.getDrugId(), item.getItemSellQuantity());
			} else if (item.getDrugUnit().equalsIgnoreCase(DrugType.STRIP.toString())) {
				stock = stockRepository.findStockWithWholeQnty(item.getDrugId(), item.getItemSellQuantity());
			}
			if (stock == null) {
				throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No stock found for given drug id");
			}
			try {
				if (item.getDrugUnit().equalsIgnoreCase(DrugType.TRIMMED.toString())) {
					reqQntyInTrimmed = item.getItemSellQuantity();
					avlQntyInTrimmed = stock.getAvlQntyTrimmed() - reqQntyInTrimmed;
					reqQntyInWhole = stock.getAvlQntyWhole() - (reqQntyInTrimmed / drugData.getPacking());
					if (reqQntyInTrimmed % stock.getPacking() == 0) {

						avlQntyInWhole = stock.getAvlQntyWhole() - reqQntyInWhole;

					} else {

						avlQntyInWhole = avlQntyInTrimmed / stock.getPacking();
					}

					itemSellPriceBeforeDiscount = (float) ((stock.getMrp() / stock.getPacking())
							* item.getItemSellQuantity());

					// Discount calculation
					if (item.getIsDiscountApplicable()) {

						itemSellPriceAfterDiscount = BaseUtil.calculatePriceAfterDiscount(stock.getMrp(),
								wrapper.getDiscount(), itemSellPriceBeforeDiscount);

						// GST calculation
						if (item.getIsGSTApplicable()) {
							itemSellPriceAfterTax = BaseUtil.calculateNetPriceAfterGST(itemSellPriceAfterDiscount,
									item.getGstPercentage());
						}
						itemSellPriceAfterTax = itemSellPriceAfterDiscount;

					} else {
						itemSellPriceAfterDiscount = itemSellPriceBeforeDiscount;
						// GST calculation
						if (item.getIsGSTApplicable()) {
							itemSellPriceAfterTax = BaseUtil.calculateNetPriceAfterGST(itemSellPriceAfterDiscount,
									item.getGstPercentage());
						}
						itemSellPriceAfterTax = itemSellPriceAfterDiscount;
					}

				} else if (item.getDrugUnit().equalsIgnoreCase(DrugType.STRIP.toString())) {

					reqQntyInWhole = item.getItemSellQuantity();
					reqQntyInTrimmed = reqQntyInWhole * stock.getPacking();
					avlQntyInWhole = stock.getAvlQntyWhole() - reqQntyInWhole;
					avlQntyInTrimmed = avlQntyInWhole * stock.getPacking();

					itemSellPriceBeforeDiscount = (float) (item.getItemSellQuantity() * stock.getMrp());

					// Discount calculation
					if (item.getIsDiscountApplicable()) {

						itemSellPriceAfterDiscount = BaseUtil.calculatePriceAfterDiscount(stock.getMrp(),
								wrapper.getDiscount(), itemSellPriceBeforeDiscount);

						// GST calculation
						if (item.getIsGSTApplicable()) {
							itemSellPriceAfterTax = BaseUtil.calculateNetPriceAfterGST(itemSellPriceAfterDiscount,
									item.getGstPercentage());
						} else {
							itemSellPriceAfterTax = itemSellPriceAfterDiscount;
						}
					} else {
						itemSellPriceAfterDiscount = itemSellPriceBeforeDiscount;
						// GST calculation
						if (item.getIsGSTApplicable()) {
							itemSellPriceAfterTax = BaseUtil.calculateNetPriceAfterGST(itemSellPriceAfterDiscount,
									item.getGstPercentage());
						} else {
							itemSellPriceAfterTax = itemSellPriceAfterDiscount;
						}
					}

				}

				if (item.getIsDiscountApplicable()) {
					discount = wrapper.getDiscount();
				} else {
					discount = 0;
				}

				lineItem.add(AddToCartDTORes.builder().discountPercentge(discount).drugUnit(item.getDrugUnit())
						.gstPercentage(item.getGstPercentage()).itemSellQuantity(item.getItemSellQuantity())
						.priceBeforeTaxAndDiscount(itemSellPriceBeforeDiscount)
						.sellPriceAfterTaxAndDiscount(itemSellPriceAfterTax).avlQntyTrimmed(avlQntyInTrimmed)
						.avlQntyWhole(avlQntyInWhole).brandName(drugData.getBrandName())
						.expiryDate(stock.getExpiryDate()).mrp(stock.getMrp()).drugForm(drugData.getDrugForm())
						.stockId(stock.getStockId()).packing(drugData.getPacking()).drugId(stock.getDrugId())
						.location(stock.getLocation()).build());
			} catch (Exception e) {
				log.error("problem occured while Adding item : {}", e);
				AddToCartWrapperRes rw = null;
				return rw;
			}

		}
		return AddToCartWrapperRes.builder().item(lineItem).build();

	}

	@Transactional
	public String drugDispense(DrugDispenseWrapperDTO wrapper) {

		try {
			Customer data = customerRepository.findByMobileNumber(wrapper.getMobileNumber());

			// Create new customer if not present and save audit detail
			if (data == null) {
				data = Customer.builder().mobileNumber(wrapper.getMobileNumber()).name(wrapper.getName()).build();
				em.persist(data);
				// The ID is only guaranteed to be generated at flush time.
				// Persisting an entity only makes it "attached" to the persistence context.
				em.flush();
			}

			Dispense dispense = Dispense.builder().customerId(data.getCustomerId())
					.paymentMode(wrapper.getPaymentMode()).sellBy(BaseUtil.getLoggedInuserName())
					.sellDate(LocalDate.now()).isReturned(false)
					.sellInvoiceNumber(BaseUtil.getRandomReference(ReferenceType.SELL.toString())).build();
			dispenseRepo.saveAndFlush(dispense);

			for (AddToCartDTORes item : wrapper.getItem()) {

				if (item == null) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No drug selected for dispense");
				}

				Query q = em
						.createNativeQuery(
								"UPDATE stock SET avl_qnty_trimmed=?, avl_qnty_whole=? WHERE stock_id =? and drug_id=?")
						.setParameter(1, item.getAvlQntyTrimmed()).setParameter(2, item.getAvlQntyWhole())
						.setParameter(3, item.getStockId()).setParameter(4, item.getDrugId());
				q.executeUpdate();

				DispenseLineItems dispenseLineITem = DispenseLineItems.builder().brandName(item.getBrandName())
						.discount(item.getDiscountPercentge()).drugForm(item.getDrugForm()).drugId(item.getDrugId())
						.drugUnit(item.getDrugUnit()).expiryDate(item.getExpiryDate()).stockId(item.getStockId())
						.itemSellQuantity(item.getItemSellQuantity()).location(item.getLocation()).mrp(item.getMrp())
						.packing(item.getPacking()).unitPrice(item.getMrp() / item.getPacking()).isReturned(false)
						.itemSellPrice(item.getSellPriceAfterTaxAndDiscount()).dispenseId(dispense.getDispenseId())
						.poId(item.getPoId()).build();
				dispenseLineItemRepository.saveAndFlush(dispenseLineITem);

				// Auditing
				auditRepository
						.save(Audit.builder().customerId(data.getCustomerId()).createdBy(BaseUtil.getLoggedInuserName())
								.createdDate(LocalDate.now()).dispenseId(dispense.getDispenseId())
								.auditType(AuditType.STOCK_OUT.toString()).stockId(item.getStockId()).build());

			}

			if (wrapper.getIsInvoiceRequired()) {

				if (wrapper.getInvoiceType().equalsIgnoreCase(PRINT)) {
					documentService.printInvoice();
				} else if (wrapper.getInvoiceType().equalsIgnoreCase(EMAIL)) {
					documentService.mailInvoice();
				}

			}
			return "Item dispensed Sucessfully";
		} catch (Exception e) {
			log.error("problem occured while dispensing item : {}", e);
			return "problem occured while dispensing item";
		}

	}

	public List<DispenseResponseDTO> getAllDispensedData(String search, PageRequest page) {

		List<Dispense> result = new ArrayList<Dispense>();
		try {
			if (search == null || search.isEmpty()) {

				result = dispenseRepo.findAll(page).getContent();

			} else {

				result = dispenseRepo.findAll(toPredicate(search, QDispense.dispense), page).getContent();
			}
		} catch (Exception e) {
			log.error("Exception occured : {}", e);
		}
		if (result.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No dispensed data available");
		}

		List<DispenseResponseDTO> response = new ArrayList<DispenseResponseDTO>();
		List<DispenseLineItemsDTO> res = new ArrayList<DispenseLineItemsDTO>();
		List<DispenseLineItemsDTO> parsedRes = new ArrayList<DispenseLineItemsDTO>();
		List<DispenseLineItemsDTO> temp = new ArrayList<DispenseLineItemsDTO>();

		try {
			// get all dispensed line items
			for (Dispense data : result) {
				for (DispenseLineItems st : data.getDispenseLineItems()) {

					res.add(DispenseLineItemsDTO.builder().brandName(st.getBrandName()).discount(st.getDiscount())
							.dispenseId(st.getDispenseId()).dispenseLineItemId(st.getDispenseLineItemId())
							.drugForm(st.getDrugForm()).drugId(st.getDrugId()).drugUnit(st.getDrugUnit())
							.expiryDate(st.getExpiryDate()).itemSellPrice(st.getItemSellPrice())
							.itemSellQuantity(st.getItemSellQuantity()).location(st.getLocation()).mrp(st.getMrp())
							.packing(st.getPacking()).unitPrice(st.getUnitPrice()).build());

				}

			}
			// sort line items by respective dispense id
			for (Dispense d : result) {
				temp.clear();
				for (DispenseLineItemsDTO x : res) {
					if (d.getDispenseId().equals(x.getDispenseId())) {
						temp.add(x);
						parsedRes = temp.stream().map(y -> new DispenseLineItemsDTO(y)).collect(Collectors.toList());
					}

				}

				Customer cust = customerRepository.findById(d.getCustomerId()).orElse(null);
				if (cust == null) {
					throw new ResponseStatusException(HttpStatus.NO_CONTENT,
							"No customer found assosited with sold items ");
				}

				response.add(DispenseResponseDTO.builder().customerId(d.getCustomerId()).customerName(cust.getName())
						.dispenseId(d.getDispenseId()).dispenseLineItems(parsedRes).mobileNumber(cust.getMobileNumber())
						.paymentMode(d.getPaymentMode()).sellBy(d.getSellBy()).sellDate(d.getSellDate())
						.sellInvoiceNumber(d.getSellInvoiceNumber()).build());
			}

			return response;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception occured", e);
		}

	}

	@Transactional
	public String returnDispensedItems(Integer dispenseId, Integer[] dispenseLineItemId) {
		try {
			Double returnedQntyTrimmed = null, returnedQntyWhole = null;
			if (dispenseId == null && dispenseLineItemId.length == 0) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Dispense and lineItems id can't be null");
			}

			Dispense dispensedData = dispenseRepo.findById(dispenseId).orElse(null);

			if (dispensedData == null) {
				throw new ResponseStatusException(HttpStatus.NO_CONTENT,
						"No drug dispensed record found for id : " + dispenseId);

			}
			dispensedData.setIsReturned(true);
			dispenseRepo.save(dispensedData);
			// Auditing Return
			auditRepository.save(Audit.builder().auditType(AuditType.ITEM_RETURNED.toString())
					.createdBy(BaseUtil.getLoggedInuserName()).createdDate(LocalDate.now())
					.customerId(dispensedData.getCustomerId()).dispenseId(dispenseId).build());

			for (DispenseLineItems lineItems : dispensedData.getDispenseLineItems()) {
				for (Integer disLineItemId : dispenseLineItemId) {

					if (disLineItemId.equals(lineItems.getDispenseLineItemId())) {

						if (lineItems.getDrugUnit().equalsIgnoreCase(DrugType.STRIP.toString())) {

							returnedQntyWhole = lineItems.getItemSellQuantity();
							returnedQntyTrimmed = returnedQntyWhole * lineItems.getPacking();

						} else if (lineItems.getDrugUnit().equalsIgnoreCase(DrugType.TRIMMED.toString())) {
							returnedQntyTrimmed = lineItems.getItemSellQuantity();
							returnedQntyWhole = returnedQntyTrimmed / lineItems.getPacking();
						}
						Stock stock = stockRepository.findById(lineItems.getStockId()).orElse(null);

						// if stock not available create new one

						if (stock == null) {

							Stock s = Stock.builder().avlQntyTrimmed(returnedQntyTrimmed)
									.avlQntyWhole(returnedQntyWhole).createddBy(BaseUtil.getLoggedInuserName())
									.distributerId(0).drugId(lineItems.getDrugId()).drugName(lineItems.getBrandName())
									.expiryDate(lineItems.getExpiryDate()).stockType(StockType.RETURN_NEW.toString())
									.invoiceReference(BaseUtil.getRandomReference(ReferenceType.SELL.toString()))
									.location(lineItems.getLocation()).mrp(lineItems.getMrp())
									.packing(lineItems.getPacking()).poId(lineItems.getPoId()).build();
							stockRepository.saveAndFlush(s);
							// Update dispense Line item status
							lineItems.setIsReturned(true);
							dispenseLineItemRepository.save(lineItems);
							auditRepository.save(Audit.builder().auditType(AuditType.RETURN_NEW.toString())
									.createdBy(BaseUtil.getLoggedInuserName()).createdDate(LocalDate.now())
									.customerId(dispensedData.getCustomerId()).dispenseId(dispenseId)
									.stockId(s.getStockId()).build());
						} else {

							stock.setAvlQntyTrimmed(stock.getAvlQntyTrimmed() - returnedQntyTrimmed);
							stock.setAvlQntyWhole(stock.getAvlQntyWhole() - returnedQntyWhole);
							stock.setUpdatedBy(BaseUtil.getLoggedInuserName());
							stock.setStockUpdatedAt(LocalDate.now());
							stock.setStockType(StockType.RETURN_UPDATE.toString());
							stockRepository.save(stock);
							lineItems.setIsReturned(true);
							dispenseLineItemRepository.save(lineItems);
							auditRepository.save(Audit.builder().auditType(AuditType.RETURN_UPDATE.toString())
									.createdBy(BaseUtil.getLoggedInuserName()).createdDate(LocalDate.now())
									.customerId(dispensedData.getCustomerId()).dispenseId(dispenseId)
									.stockId(stock.getStockId()).build());
						}
						return "Success in returning item";

					}

				}

			}
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception occured", e);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception occured", e);
		}
		return "Failure while returning item";
	}
}
