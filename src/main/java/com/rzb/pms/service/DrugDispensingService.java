package com.rzb.pms.service;

import java.util.ArrayList;
import java.util.List;

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
import com.rzb.pms.model.Customer;
import com.rzb.pms.model.Drug;
import com.rzb.pms.model.DrugDispense;
import com.rzb.pms.model.SellAudit;
import com.rzb.pms.model.Stock;
import com.rzb.pms.repository.DrugDispenseRepository;
import com.rzb.pms.repository.DrugRepository;
import com.rzb.pms.repository.SellAuditRepository;
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
	private SellAuditRepository auditRepository;

	@Autowired
	private StockRepository stockRepository;

	@PersistenceContext
	private EntityManager em;

	@Transactional
	public String drugDispense(DrugDispense item) {

		if (item == null) {

			logger.error("No line item Added");
			throw new CustomException("No line item Added", HttpStatus.BAD_REQUEST);
		}
		try {
			Double reqQntyInTrimmed = null, avlQntyInTrimmed = null, avlQntyInWhole = null, reqQntyInWhole = null;
			String location = null;
			Integer stockId = null;
			Drug drugData = repository.findById(item.getDrugId()).get();
			List<Stock> sdata = new ArrayList<Stock>();
			if (item.getDrugUnit().equalsIgnoreCase(DrugType.TRIMMED.toString())) {
				sdata = stockRepository.findStockWithTrimmedQnty(drugData.getDrugId(), item.getItemSellQuantity());
			} else if (item.getDrugUnit().equalsIgnoreCase(DrugType.STRIP.toString())) {
				sdata = stockRepository.findStockWithWholeQnty(drugData.getDrugId(), item.getItemSellQuantity());
			}
			for (Stock stock : sdata) {
				location = stock.getLocation();
				stockId = stock.getStockId();
				if (item.getDrugUnit().equalsIgnoreCase(DrugType.TRIMMED.toString())) {
					reqQntyInTrimmed = item.getItemSellQuantity();
					avlQntyInTrimmed = stock.getAvlQntyTrimmed() - reqQntyInTrimmed;
					reqQntyInWhole = stock.getAvlQntyWhole() - (reqQntyInTrimmed / drugData.getPacking());
					if (reqQntyInTrimmed % stock.getPacking() == 0) {

						avlQntyInWhole = stock.getAvlQntyWhole() - (reqQntyInTrimmed / stock.getPacking());

					} else {

						avlQntyInWhole = avlQntyInTrimmed / stock.getPacking();
					}

				} else {

					reqQntyInWhole = item.getItemSellQuantity();
					reqQntyInTrimmed = reqQntyInWhole * stock.getPacking();
					avlQntyInWhole = stock.getAvlQntyWhole() - reqQntyInWhole;
					avlQntyInTrimmed = avlQntyInWhole * stock.getPacking();
				}
			}
			float itemSellPrice = BaseUtil.calculatePriceAfterDiscount(item.getMrp(), item.getDiscount());
			em.createNativeQuery(
					"UPDATE stock SET avl_qnty_trimmed=?, avl_qnty_whole=? WHERE stock_id =? and drug_id=?")
					.setParameter(1, avlQntyInTrimmed).setParameter(2, avlQntyInWhole)
					.setParameter(3, drugData.getDrugId()).setParameter(4, stockId).executeUpdate();
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
					.reqQntyInTrimmed(reqQntyInTrimmed).location(location).mrp(drugData.getMrp())
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
