package com.rzb.pms.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.rzb.pms.dto.AddToCartDTOReq;
import com.rzb.pms.dto.AddToCartDTORes;
import com.rzb.pms.dto.AddToCartWrapperReq;
import com.rzb.pms.dto.AddToCartWrapperRes;
import com.rzb.pms.dto.DrugDispenseWrapperDTO;
import com.rzb.pms.model.Audit;
import com.rzb.pms.model.Customer;
import com.rzb.pms.model.Dispense;
import com.rzb.pms.model.DispenseLineItems;
import com.rzb.pms.model.Drug;
import com.rzb.pms.model.Stock;
import com.rzb.pms.model.enums.AuditType;
import com.rzb.pms.model.enums.DrugType;
import com.rzb.pms.model.enums.ReferenceType;
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

		try {

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

			}
			return AddToCartWrapperRes.builder().item(lineItem).build();

		} catch (Exception e) {
			log.error("problem occured while Adding item : {}", e);
			AddToCartWrapperRes rw = null;
			return rw;
		}

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

			Dispense dispense = Dispense.builder().customerId(data.getCustomerId()).build();
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
						.customerName(wrapper.getName()).discount(item.getDiscountPercentge())
						.drugForm(item.getDrugForm()).drugId(item.getDrugId()).drugUnit(item.getDrugUnit())
						.expiryDate(item.getExpiryDate()).itemSellQuantity(item.getItemSellQuantity())
						.location(item.getLocation()).mobileNumber(wrapper.getMobileNumber()).mrp(item.getMrp())
						.packing(item.getPacking()).paymentMode(wrapper.getPaymentMode())
						.sellBy(BaseUtil.getLoggedInuserName()).sellDate(LocalDate.now())
						.unitPrice(item.getMrp() / item.getPacking())
						.itemSellPrice(item.getSellPriceAfterTaxAndDiscount())
						.sellInvoiceNumber(BaseUtil.getRandomReference(ReferenceType.SELL.toString())).build();
				dispenseLineItemRepository.saveAndFlush(dispenseLineITem);

				// Auditing
				auditRepository
						.save(Audit.builder().customerId(data.getCustomerId()).createdBy(BaseUtil.getLoggedInuserName())
								.createdDate(LocalDate.now()).dispenseId(dispense.getDispenseId())
								.auditType(AuditType.SELL.toString()).stockId(item.getStockId()).build());

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

}
