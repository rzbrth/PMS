package com.rzb.pms.service;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.rzb.pms.dto.DrugType;
import com.rzb.pms.exception.CustomException;
import com.rzb.pms.log.Log;
import com.rzb.pms.model.AddToCart;
import com.rzb.pms.model.Customer;
import com.rzb.pms.model.Drug;
import com.rzb.pms.model.SellAudit;
import com.rzb.pms.repository.AddToCartRepository;
import com.rzb.pms.repository.CustomerRepository;
import com.rzb.pms.repository.DrugRepository;
import com.rzb.pms.repository.SellAuditRepository;

@Service
public class AddToCartService {

	@Log
	private Logger logger;

	@Autowired
	private AddToCartRepository cartRepository;

	@Autowired
	private DrugRepository repository;

	@Autowired
	private SellAuditRepository auditRepository;

	@Autowired
	private CustomerRepository customerRepository;

//	public String addToCart(List<AddToCart> lineItems) {
//
//		if (lineItems.isEmpty()) {
//
//			logger.error("No line item Added");
//			throw new CustomException("No line item Added", HttpStatus.BAD_REQUEST);
//		}
//		try {
//			Double reqQntyInTrimmed = null;
//			Double avlQntyInTrimmed = null;
//			Double avlQntyInWhole = null;
//			Double reqQntyInWhole = null;
//
//			for (AddToCart item : lineItems) {
//				Drug drugData = repository.findById(item.getDrugId()).get();
//
//				if (item.getDrugUnit().equalsIgnoreCase(DrugType.TRIMMED.toString())) {
//					reqQntyInTrimmed = item.getItemSellQuantity();
//					avlQntyInTrimmed = drugData.getAvlQntyInTrimmed() - reqQntyInTrimmed;
//					if (reqQntyInTrimmed % drugData.getPacking() == 0) {
//
//						avlQntyInWhole = drugData.getAvlQntyInWhole() - (reqQntyInTrimmed / drugData.getPacking());
//
//					} else {
//
//						avlQntyInWhole = avlQntyInTrimmed / drugData.getPacking();
//					}
//
//				} else {
//
//					reqQntyInWhole = item.getItemSellQuantity();
//					avlQntyInWhole = drugData.getAvlQntyInWhole() - reqQntyInWhole;
//					avlQntyInTrimmed = avlQntyInWhole * drugData.getPacking();
//				}
//
//				repository.save(Drug.builder().avlQntyInTrimmed(avlQntyInTrimmed)
//						.avlQntyInWhole(avlQntyInWhole).build());
//
//				auditRepository.save(SellAudit.builder().brandName(drugData.getBrandName())
//						.composition(drugData.getComposition())
//						.discount(item.getDiscount()).drugId(item.getDrugId()).genericName(drugData.getGenericName())
//						.itemSellPrice(item.getItemSellPrice()).itemSellQuantity(item.getItemSellQuantity())
//						.reqQntyInWhole(reqQntyInWhole).reqQntyInTrimmed(reqQntyInTrimmed)
//						.location(drugData.getLocation()).mrp(drugData.getMrp()).packing(drugData.getPacking())
//						.paymentMode(item.getPaymentMode()).sellBy(item.getSellBy()).sellDate(item.getSellDate())
//						.expiryDate(drugData.getExpiryDate()).drugForm(drugData.getDrugForm())
//						.mobileNumber(item.getMobileNumber())
//						.build());
//				customerRepository.save(Customer.builder().mobileNumber(item.getMobileNumber()).build());
//
//			}
//			cartRepository.saveAll(lineItems);
//
//			return "Item added to cart Sucessfully";
//
//		} catch (Exception e) {
//
//			throw new CustomException("problem occured while adding item to cart", e.getCause());
//		}
//
//	}

	public String addToCart(AddToCart item) {

		if (item == null) {

			logger.error("No line item Added");
			throw new CustomException("No line item Added", HttpStatus.BAD_REQUEST);
		}
		try {
			Double reqQntyInTrimmed = null;
			Double avlQntyInTrimmed = null;
			Double avlQntyInWhole = null;
			Double reqQntyInWhole = null;

			Drug drugData = repository.findById(item.getDrugId()).get();

			if (item.getDrugUnit().equalsIgnoreCase(DrugType.TRIMMED.toString())) {
				reqQntyInTrimmed = item.getItemSellQuantity();
				avlQntyInTrimmed = drugData.getAvlQntyInTrimmed() - reqQntyInTrimmed;
				if (reqQntyInTrimmed % drugData.getPacking() == 0) {

					avlQntyInWhole = drugData.getAvlQntyInWhole() - (reqQntyInTrimmed / drugData.getPacking());

				} else {

					avlQntyInWhole = avlQntyInTrimmed / drugData.getPacking();
				}

			} else {

				reqQntyInWhole = item.getItemSellQuantity();
				avlQntyInWhole = drugData.getAvlQntyInWhole() - reqQntyInWhole;
				avlQntyInTrimmed = avlQntyInWhole * drugData.getPacking();
			}

			repository.save(Drug.builder().avlQntyInTrimmed(avlQntyInTrimmed).avlQntyInWhole(avlQntyInWhole).build());

			auditRepository.save(SellAudit.builder().brandName(drugData.getBrandName())
					.composition(drugData.getComposition()).discount(item.getDiscount()).drugId(item.getDrugId())
					.genericName(drugData.getGenericName()).itemSellPrice(item.getItemSellPrice())
					.itemSellQuantity(item.getItemSellQuantity()).reqQntyInWhole(reqQntyInWhole)
					.reqQntyInTrimmed(reqQntyInTrimmed).location(drugData.getLocation()).mrp(drugData.getMrp())
					.packing(drugData.getPacking()).paymentMode(item.getPaymentMode()).sellBy(item.getSellBy())
					.sellDate(item.getSellDate()).expiryDate(drugData.getExpiryDate()).drugForm(drugData.getDrugForm())
					.mobileNumber(item.getMobileNumber()).build());
			customerRepository.save(Customer.builder().mobileNumber(item.getMobileNumber()).build());

			cartRepository.save(item);

			return "Item added to cart Sucessfully";

		} catch (Exception e) {

			throw new CustomException("problem occured while adding item to cart", e.getCause());
		}

	}

	public String getCartContent() {

		return null;

	}

}
