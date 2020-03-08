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

import com.rzb.pms.dto.AddToCartDTOReq;
import com.rzb.pms.dto.AddToCartDTORes;
import com.rzb.pms.dto.AddToCartWrapperReq;
import com.rzb.pms.dto.AddToCartWrapperRes;
import com.rzb.pms.dto.AuditType;
import com.rzb.pms.dto.DrugDispenseWrapperDTO;
import com.rzb.pms.dto.DrugType;
import com.rzb.pms.dto.ReferenceType;
import com.rzb.pms.exception.CustomEntityNotFoundException;
import com.rzb.pms.exception.CustomException;
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

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DrugDispensingService {

	

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

//	@Transactional
//	public String drugDispense(AddToCartWrapper wrapper) {
//
//		try {
//
//			// OtherInfoDTO restData = wrapper.getInfo();
//
//			for (AddToCartDTOReq item : wrapper.getItem()) {
//
//				if (item == null) {
//
//					log.error("No drug selected for dispense");
//					throw new CustomException("No drug selected for dispense", HttpStatus.BAD_REQUEST);
//				}
//				Double reqQntyInTrimmed = null, avlQntyInTrimmed = null, avlQntyInWhole = null, reqQntyInWhole = null;
//				String location = null;
//				Integer stockId = null;
//				float discount = 0;
//				float itemSellPriceBeforeDiscount = 0, itemSellPriceAfterDiscount = 0, itemSellPriceBeforeTax = 0,
//						itemSellPriceAfterTax = 0;
//				Drug drugData = repository.findById(item.getDrugId()).get();
//				Stock stock = null;
//				if (item.getDrugUnit().equalsIgnoreCase(DrugType.TRIMMED.toString())) {
//					stock = stockRepository.findStockWithTrimmedQnty(item.getDrugId(), item.getItemSellQuantity());
//				} else if (item.getDrugUnit().equalsIgnoreCase(DrugType.STRIP.toString())) {
//					stock = stockRepository.findStockWithWholeQnty(item.getDrugId(), item.getItemSellQuantity());
//				}
//				if (stock == null) {
//					log.error("Stock is empty for : " + drugData.getBrandName());
//					throw new CustomEntityNotFoundException(Stock.class, "Brand", drugData.getBrandName().toString());
//
//				}
//
//				location = stock.getLocation();
//				stockId = stock.getStockId();
//				if (item.getDrugUnit().equalsIgnoreCase(DrugType.TRIMMED.toString())) {
//					reqQntyInTrimmed = item.getItemSellQuantity();
//					avlQntyInTrimmed = stock.getAvlQntyTrimmed() - reqQntyInTrimmed;
//					reqQntyInWhole = stock.getAvlQntyWhole() - (reqQntyInTrimmed / drugData.getPacking());
//					if (reqQntyInTrimmed % stock.getPacking() == 0) {
//
//						avlQntyInWhole = stock.getAvlQntyWhole() - reqQntyInWhole;
//
//					} else {
//
//						avlQntyInWhole = avlQntyInTrimmed / stock.getPacking();
//					}
//
//					itemSellPriceBeforeDiscount = (float) ((stock.getMrp() / stock.getPacking())
//							* item.getItemSellQuantity());
//
//					// Discount calculation
//					if (item.getIsDiscountApplicable()) {
//
//						itemSellPriceAfterDiscount = BaseUtil.calculatePriceAfterDiscount(stock.getMrp(),
//								wrapper.getDiscount(), itemSellPriceBeforeDiscount);
//
//						// GST calculation
//						if (item.getIsGSTApplicable()) {
//							itemSellPriceAfterTax = BaseUtil.calculateNetPriceAfterGST(itemSellPriceAfterDiscount,
//									item.getGstPercentage());
//						}
//						itemSellPriceAfterTax = itemSellPriceAfterDiscount;
//
//					} else {
//						itemSellPriceAfterDiscount = itemSellPriceBeforeDiscount;
//						// GST calculation
//						if (item.getIsGSTApplicable()) {
//							itemSellPriceAfterTax = BaseUtil.calculateNetPriceAfterGST(itemSellPriceAfterDiscount,
//									item.getGstPercentage());
//						}
//						itemSellPriceAfterTax = itemSellPriceAfterDiscount;
//					}
//
//				} else if (item.getDrugUnit().equalsIgnoreCase(DrugType.STRIP.toString())) {
//
//					reqQntyInWhole = item.getItemSellQuantity();
//					reqQntyInTrimmed = reqQntyInWhole * stock.getPacking();
//					avlQntyInWhole = stock.getAvlQntyWhole() - reqQntyInWhole;
//					avlQntyInTrimmed = avlQntyInWhole * stock.getPacking();
//
//					itemSellPriceBeforeDiscount = (float) (item.getItemSellQuantity() * stock.getMrp());
//
//					// Discount calculation
//					if (item.getIsDiscountApplicable()) {
//
//						itemSellPriceAfterDiscount = BaseUtil.calculatePriceAfterDiscount(stock.getMrp(),
//								wrapper.getDiscount(), itemSellPriceBeforeDiscount);
//
//						// GST calculation
//						if (item.getIsGSTApplicable()) {
//							itemSellPriceAfterTax = BaseUtil.calculateNetPriceAfterGST(itemSellPriceAfterDiscount,
//									item.getGstPercentage());
//						}
//						itemSellPriceAfterTax = itemSellPriceAfterDiscount;
//					} else {
//						itemSellPriceAfterDiscount = itemSellPriceBeforeDiscount;
//						// GST calculation
//						if (item.getIsGSTApplicable()) {
//							itemSellPriceAfterTax = BaseUtil.calculateNetPriceAfterGST(itemSellPriceAfterDiscount,
//									item.getGstPercentage());
//						}
//						itemSellPriceAfterTax = itemSellPriceAfterDiscount;
//					}
//
//				}
//				// Updating drug stock
//				Query q = em
//						.createNativeQuery(
//								"UPDATE stock SET avl_qnty_trimmed=?, avl_qnty_whole=? WHERE stock_id =? and drug_id=?")
//						.setParameter(1, avlQntyInTrimmed).setParameter(2, avlQntyInWhole).setParameter(3, stockId)
//						.setParameter(4, drugData.getDrugId());
//				q.executeUpdate();
//
//				Customer data = customerRepository.findByMobileNumber(wrapper.getCustomerMobileNumber());
//
//				if (item.getIsDiscountApplicable()) {
//					discount = wrapper.getDiscount();
//				} else {
//					discount = 0;
//				}
//				DrugDispense dispense = DrugDispense.builder().brandName(drugData.getBrandName())
//						.composition(drugData.getComposition()).customerName(wrapper.getCustomerName())
//						.discount(discount).drugForm(drugData.getDrugForm()).drugId(item.getDrugId())
//						.drugUnit(item.getDrugUnit()).expiryDate(stock.getExpiryDate())
//						.genericName(drugData.getGenericName()).itemSellPrice(itemSellPriceAfterDiscount)
//						.itemSellQuantity(item.getItemSellQuantity()).location(location)
//						.mobileNumber(wrapper.getCustomerMobileNumber()).mrp(stock.getMrp()).packing(stock.getPacking())
//						.paymentMode(wrapper.getPaymentMode()).sellBy("").unitPrice(stock.getMrp() / stock.getPacking())
//						.sellInvoiceNumber(BaseUtil.getRandomReference(ReferenceType.SELL.toString())).build();
//				dispenseRepository.saveAndFlush(dispense);
//				// Create new customer if not present and save audit detail
//				if (data == null) {
//					Customer customer = Customer.builder().mobileNumber(wrapper.getCustomerMobileNumber())
//							.name(wrapper.getCustomerName()).build();
//					em.persist(customer);
//					// The ID is only guaranteed to be generated at flush time.
//					// Persisting an entity only makes it "attached" to the persistence context.
//					em.flush();
//
//					auditRepository.save(Audit.builder().customerId(customer.getCustomerId()).createdBy("")
//							.createdDate(new Date()).dispenseId(dispense.getDispenseId())
//							.auditType(AuditType.SELL.toString()).stockId(stockId).build());
//				} else {
//					auditRepository.save(Audit.builder().customerId(data.getCustomerId()).createdBy("")
//							.createdDate(new Date()).dispenseId(dispense.getDispenseId())
//							.auditType(AuditType.SELL.toString()).stockId(stockId).build());
//				}
//			}
//
//			if (wrapper.getIsInvoiceRequired()) {
//
//				if (wrapper.getInvoiceType().equalsIgnoreCase(PRINT)) {
//					documentService.printInvoice();
//				} else if (wrapper.getInvoiceType().equalsIgnoreCase(EMAIL)) {
//					documentService.mailInvoice();
//				}
//
//			}
//		} catch (Exception e) {
//			log.error("problem occured while dispensing item", e);
//			throw new CustomException("problem occured while dispensing item", e);
//		}
//		return "Item dispensed Sucessfully";
//
//	}

	public AddToCartWrapperRes addToCard(AddToCartWrapperReq wrapper) {

		try {

			// OtherInfoDTO restData = wrapper.getInfo();
			List<AddToCartDTORes> lineItem = new ArrayList<AddToCartDTORes>();
			for (AddToCartDTOReq item : wrapper.getItem()) {

				if (item == null) {

					log.error("No drug selected for dispense");
					throw new CustomException("No drug selected for dispense", HttpStatus.BAD_REQUEST);
				}
				Double reqQntyInTrimmed = null, avlQntyInTrimmed = null, avlQntyInWhole = null, reqQntyInWhole = null;

				float discount = 0;
				float itemSellPriceBeforeDiscount = 0, itemSellPriceAfterDiscount = 0, itemSellPriceAfterTax = 0;
				Drug drugData = repository.findById(item.getDrugId()).get();
				Stock stock = null;
				if (item.getDrugUnit().equalsIgnoreCase(DrugType.TRIMMED.toString())) {
					stock = stockRepository.findStockWithTrimmedQnty(item.getDrugId(), item.getItemSellQuantity());
				} else if (item.getDrugUnit().equalsIgnoreCase(DrugType.STRIP.toString())) {
					stock = stockRepository.findStockWithWholeQnty(item.getDrugId(), item.getItemSellQuantity());
				}
				if (stock == null) {
					log.error("Stock is empty for: {}",drugData.getBrandName());
					throw new CustomEntityNotFoundException(Stock.class, "Brand", drugData.getBrandName().toString());

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
						.composition(drugData.getComposition()).expiryDate(stock.getExpiryDate()).mrp(stock.getMrp())
						.drugForm(drugData.getDrugForm()).stockId(stock.getStockId()).packing(drugData.getPacking())
						.genericName(drugData.getGenericName()).drugId(stock.getDrugId()).location(stock.getLocation())
						.build());

			}
			return AddToCartWrapperRes.builder().item(lineItem).build();

		} catch (Exception e) {
			log.error("problem occured while Adding item", e);
			throw new CustomException("problem occured while Adding item", e);
		}

	}

	@Transactional
	public String drugDispense(DrugDispenseWrapperDTO wrapper) {

		try {

			for (AddToCartDTORes item : wrapper.getItem()) {

				if (item == null) {

					log.error("No drug selected for dispense");
					throw new CustomException("No drug selected for dispense", HttpStatus.BAD_REQUEST);
				}

				Query q = em
						.createNativeQuery(
								"UPDATE stock SET avl_qnty_trimmed=?, avl_qnty_whole=? WHERE stock_id =? and drug_id=?")
						.setParameter(1, item.getAvlQntyTrimmed()).setParameter(2, item.getAvlQntyWhole())
						.setParameter(3, item.getStockId()).setParameter(4, item.getDrugId());
				q.executeUpdate();

				Customer data = customerRepository.findByMobileNumber(wrapper.getMobileNumber());

				DrugDispense dispense = DrugDispense.builder().brandName(item.getBrandName())
						.composition(item.getComposition()).customerName(wrapper.getName())
						.discount(item.getDiscountPercentge()).drugForm(item.getDrugForm()).drugId(item.getDrugId())
						.drugUnit(item.getDrugUnit()).expiryDate(item.getExpiryDate())
						.genericName(item.getGenericName()).itemSellQuantity(item.getItemSellQuantity())
						.location(item.getLocation()).mobileNumber(wrapper.getMobileNumber()).mrp(item.getMrp())
						.packing(item.getPacking()).paymentMode(wrapper.getPaymentMode()).sellBy("")
						.sellDate(LocalDate.now()).unitPrice(item.getMrp() / item.getPacking())
						.itemSellPrice(item.getSellPriceAfterTaxAndDiscount())
						.sellInvoiceNumber(BaseUtil.getRandomReference(ReferenceType.SELL.toString())).build();
				dispenseRepository.saveAndFlush(dispense);
				// Create new customer if not present and save audit detail
				if (data == null) {
					Customer customer = Customer.builder().mobileNumber(wrapper.getMobileNumber())
							.name(wrapper.getName()).build();
					em.persist(customer);
					// The ID is only guaranteed to be generated at flush time.
					// Persisting an entity only makes it "attached" to the persistence context.
					em.flush();

					auditRepository.save(Audit.builder().customerId(customer.getCustomerId()).createdBy("")
							.createdDate(LocalDate.now()).dispenseId(dispense.getDispenseId())
							.auditType(AuditType.SELL.toString()).stockId(item.getStockId()).build());
				} else {
					auditRepository.save(Audit.builder().customerId(data.getCustomerId()).createdBy("")
							.createdDate(LocalDate.now()).dispenseId(dispense.getDispenseId())
							.auditType(AuditType.SELL.toString()).stockId(item.getStockId()).build());
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
			log.error("problem occured while dispensing item", e);
			throw new CustomException("problem occured while dispensing item", e);
		}
		return "Item dispensed Sucessfully";

	}

}
