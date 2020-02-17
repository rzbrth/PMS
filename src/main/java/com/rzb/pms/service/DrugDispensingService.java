package com.rzb.pms.service;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.rzb.pms.dto.AddToCartWrapper;
import com.rzb.pms.dto.AuditType;
import com.rzb.pms.dto.DrugDispenseDTO;
import com.rzb.pms.dto.DrugType;
import com.rzb.pms.dto.ReferenceType;
import com.rzb.pms.exception.CustomEntityNotFoundException;
import com.rzb.pms.exception.CustomException;
import com.rzb.pms.log.Log;
import com.rzb.pms.model.Audit;
import com.rzb.pms.model.Customer;
import com.rzb.pms.model.Drug;
import com.rzb.pms.model.DrugDispense;
import com.rzb.pms.model.Stock;
import com.rzb.pms.repository.AuditRepository;
import com.rzb.pms.repository.CustomerRepository;
import com.rzb.pms.repository.DrugDispenseRepository;
import com.rzb.pms.repository.DrugRepository;
import com.rzb.pms.repository.StockRepository;
import com.rzb.pms.utils.BaseUtil;

@Service
public class DrugDispensingService {

	@Log
	private Logger logger;

	@Autowired
	private DrugDispenseRepository dispenseRepository;

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

	@Transactional
	public String drugDispense(AddToCartWrapper wrapper) {

		try {

			// OtherInfoDTO restData = wrapper.getInfo();

			for (DrugDispenseDTO item : wrapper.getItem()) {

				if (item == null) {

					logger.error("No drug selected for dispense");
					throw new CustomException("No drug selected for dispense", HttpStatus.BAD_REQUEST);
				}
				Double reqQntyInTrimmed = null, avlQntyInTrimmed = null, avlQntyInWhole = null, reqQntyInWhole = null;
				String location = null;
				Integer stockId = null;
				float discount = 0;
				float itemSellPriceBeforeDiscount = 0, itemSellPriceAfterDiscount = 0;
				Drug drugData = repository.findById(item.getDrugId()).get();
				Stock stock = null;
				if (item.getDrugUnit().equalsIgnoreCase(DrugType.TRIMMED.toString())) {
					stock = stockRepository.findStockWithTrimmedQnty(item.getDrugId(), item.getItemSellQuantity());
				} else if (item.getDrugUnit().equalsIgnoreCase(DrugType.STRIP.toString())) {
					stock = stockRepository.findStockWithWholeQnty(item.getDrugId(), item.getItemSellQuantity());
				}
				if (stock == null) {
					logger.error("Stock is empty for : " + drugData.getBrandName());
					throw new CustomEntityNotFoundException(Stock.class, "Brand", drugData.getBrandName().toString());

				}

				location = stock.getLocation();
				stockId = stock.getStockId();
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

					if (item.getIsDiscountApplicable()) {

						itemSellPriceAfterDiscount = BaseUtil.calculatePriceAfterDiscount(stock.getMrp(),
								wrapper.getDiscount(), itemSellPriceBeforeDiscount);
						discount = wrapper.getDiscount();
					} else {
						itemSellPriceAfterDiscount = itemSellPriceBeforeDiscount;
						discount = 0;
					}

				} else if (item.getDrugUnit().equalsIgnoreCase(DrugType.STRIP.toString())) {

					reqQntyInWhole = item.getItemSellQuantity();
					reqQntyInTrimmed = reqQntyInWhole * stock.getPacking();
					avlQntyInWhole = stock.getAvlQntyWhole() - reqQntyInWhole;
					avlQntyInTrimmed = avlQntyInWhole * stock.getPacking();

					itemSellPriceBeforeDiscount = (float) (item.getItemSellQuantity() * stock.getMrp());
					if (item.getIsDiscountApplicable()) {

						itemSellPriceAfterDiscount = BaseUtil.calculatePriceAfterDiscount(stock.getMrp(),
								wrapper.getDiscount(), itemSellPriceBeforeDiscount);
					}

				}
				// Updating drug stock
				Query q = em
						.createNativeQuery(
								"UPDATE stock SET avl_qnty_trimmed=?, avl_qnty_whole=? WHERE stock_id =? and drug_id=?")
						.setParameter(1, avlQntyInTrimmed).setParameter(2, avlQntyInWhole).setParameter(3, stockId)
						.setParameter(4, drugData.getDrugId());
				q.executeUpdate();

				Customer data = customerRepository.findByMobileNumber(wrapper.getCustomerMobileNumber());

				if (item.getIsDiscountApplicable()) {
					discount = wrapper.getDiscount();
				} else {
					discount = 0;
				}
				DrugDispense dispense = DrugDispense.builder().brandName(drugData.getBrandName())
						.composition(drugData.getComposition()).customerName(wrapper.getCustomerName())
						.discount(discount).drugForm(drugData.getDrugForm()).drugId(item.getDrugId())
						.drugUnit(item.getDrugUnit()).expiryDate(stock.getExpiryDate())
						.genericName(drugData.getGenericName()).itemSellPrice(itemSellPriceAfterDiscount)
						.itemSellQuantity(item.getItemSellQuantity()).location(location)
						.mobileNumber(wrapper.getCustomerMobileNumber()).mrp(stock.getMrp()).packing(stock.getPacking())
						.paymentMode(wrapper.getPaymentMode()).sellBy("").unitPrice(stock.getMrp() / stock.getPacking())
						.sellInvoiceNumber(BaseUtil.getRandomReference(ReferenceType.SELL.toString())).build();
				dispenseRepository.saveAndFlush(dispense);
				// Create new customer if not present and save audit detail
				if (data == null) {
					Customer customer = Customer.builder().mobileNumber(wrapper.getCustomerMobileNumber())
							.name(wrapper.getCustomerName()).build();
					em.persist(customer);
					// The ID is only guaranteed to be generated at flush time.
					// Persisting an entity only makes it "attached" to the persistence context.
					em.flush();

					auditRepository.save(Audit.builder().customerId(customer.getCustomerId()).createdBy("")
							.createdDate(new Date()).dispenseId(dispense.getDispenseId())
							.auditType(AuditType.SELL.toString()).stockId(stockId).build());
				} else {
					auditRepository.save(Audit.builder().customerId(data.getCustomerId()).createdBy("")
							.createdDate(new Date()).dispenseId(dispense.getDispenseId())
							.auditType(AuditType.SELL.toString()).stockId(stockId).build());
				}
			}

			if (wrapper.getIsInvoiceRequired()) {

				if (wrapper.getInvoiceType().equalsIgnoreCase(PRINT)) {
					documentService.printInvoice();
				} else if (wrapper.getInvoiceType().equalsIgnoreCase(EMAIL)) {
					documentService.mailInvoice();
				}

			}
		} catch (Exception e) {
			logger.error("problem occured while dispensing item", e);
			throw new CustomException("problem occured while dispensing item", e);
		}
		return "Item dispensed Sucessfully";

	}

}
