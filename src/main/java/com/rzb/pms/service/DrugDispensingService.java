package com.rzb.pms.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.rzb.pms.dto.DrugType;
import com.rzb.pms.exception.CustomException;
import com.rzb.pms.log.Log;
import com.rzb.pms.model.DrugDispense;
import com.rzb.pms.model.Customer;
import com.rzb.pms.model.Drug;
import com.rzb.pms.model.SellAudit;
import com.rzb.pms.repository.DrugDispenseRepository;
import com.rzb.pms.repository.CustomerRepository;
import com.rzb.pms.repository.DrugRepository;
import com.rzb.pms.repository.SellAuditRepository;
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
	private SellAuditRepository auditRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@PersistenceContext
	private EntityManager em;

	@Transactional
	public String drugDispense(DrugDispense item) {

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
				reqQntyInWhole = drugData.getAvlQntyInWhole() - (reqQntyInTrimmed / drugData.getPacking());
				if (reqQntyInTrimmed % drugData.getPacking() == 0) {

					avlQntyInWhole = drugData.getAvlQntyInWhole() - (reqQntyInTrimmed / drugData.getPacking());

				} else {

					avlQntyInWhole = avlQntyInTrimmed / drugData.getPacking();
				}

			} else {

				reqQntyInWhole = item.getItemSellQuantity();
				reqQntyInTrimmed = reqQntyInWhole * drugData.getPacking();
				avlQntyInWhole = drugData.getAvlQntyInWhole() - reqQntyInWhole;
				avlQntyInTrimmed = avlQntyInWhole * drugData.getPacking();
			}
			float itemSellPrice = BaseUtil.calculatePriceAfterDiscount(item.getMrp(), item.getDiscount());
			em.createNativeQuery("UPDATE drug SET avl_qnty_in_trimmed=?, avl_qnty_in_whole=? WHERE drug_id=?")
					.setParameter(1, avlQntyInTrimmed).setParameter(2, avlQntyInWhole)
					.setParameter(3, drugData.getDrugId()).executeUpdate();
			Customer customer = Customer.builder().mobileNumber(item.getMobileNumber()).name(item.getCustomerName())
					.build();
			// customerRepository.save(customer);
			em.persist(customer);
			// The ID is only guaranteed to be generated at flush time.
			// Persisting an entity only makes it "attached" to the persistence context.
			em.flush();

			auditRepository.save(SellAudit.builder().brandName(drugData.getBrandName())
					.composition(drugData.getComposition()).discount(item.getDiscount()).drugId(item.getDrugId())
					.genericName(drugData.getGenericName()).itemSellPrice(itemSellPrice)
					.itemSellQuantity(item.getItemSellQuantity()).reqQntyInWhole(reqQntyInWhole)
					.reqQntyInTrimmed(reqQntyInTrimmed).location(drugData.getLocation()).mrp(drugData.getMrp())
					.packing(drugData.getPacking()).paymentMode(item.getPaymentMode()).sellBy(item.getSellBy())
					.sellDate(item.getSellDate()).expiryDate(drugData.getExpiryDate()).drugForm(drugData.getDrugForm())
					.mobileNumber(item.getMobileNumber()).customerId(customer.getCustomerId()).build());

			dispenseRepository.save(item);

			return "Item added to cart Sucessfully";

		} catch (Exception e) {
			logger.error("problem occured while adding item to cart", e);
			throw new CustomException("problem occured while adding item to cart", e);
		}

	}

	public String getCartContent() {

		return null;

	}

}
