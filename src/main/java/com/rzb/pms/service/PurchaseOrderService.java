package com.rzb.pms.service;

import static io.github.perplexhub.rsql.RSQLQueryDslSupport.toPredicate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.rzb.pms.dto.AuditType;
import com.rzb.pms.dto.OrderStatus;
import com.rzb.pms.dto.PoCreateDTO;
import com.rzb.pms.dto.PoDrugDTO;
import com.rzb.pms.dto.PoLineItemAddDTO;
import com.rzb.pms.dto.PoLineItemUpdateDTO;
import com.rzb.pms.dto.PoUpdateDTO;
import com.rzb.pms.dto.PurchaseOrderDTO;
import com.rzb.pms.dto.PurchaseOrderResponse;
import com.rzb.pms.dto.ReferenceType;
import com.rzb.pms.exception.CustomEntityNotFoundException;
import com.rzb.pms.exception.CustomException;
import com.rzb.pms.log.Log;
import com.rzb.pms.model.Audit;
import com.rzb.pms.model.PoLineItems;
import com.rzb.pms.model.PurchaseOrder;
import com.rzb.pms.model.QPurchaseOrder;
import com.rzb.pms.repository.AuditRepository;
import com.rzb.pms.repository.PoLineItemsRepository;
import com.rzb.pms.repository.PurchaseOrderRepository;
import com.rzb.pms.utils.BaseUtil;
import com.rzb.pms.utils.CollectionMapper;

@Service
public class PurchaseOrderService {

	@Log
	private Logger logger;

	@Autowired
	private PurchaseOrderRepository repository;

	@Autowired
	private EntityManager em;

	@Autowired
	private PoLineItemsRepository poLineItemsRepository;

	@Autowired
	private AuditRepository auditRepo;

	/**
	 * This is used to create Purchase Order
	 */
	@Transactional
	public String createPO(PoCreateDTO data) {

		if (data == null) {
			logger.error("Order can't be empty");
			throw new CustomException("Order can't be empty", HttpStatus.BAD_REQUEST);
		}

		try {
			/*
			 * save data to PurchaseOrder table and gate id and against that PurchaseOrder
			 * id as fk save drug request to PoLineItems.
			 */
			PurchaseOrder orderData = PurchaseOrder.builder().createdBy("").createdDate(new Date())
					.poReference(BaseUtil.getRandomReference(ReferenceType.PO.toString()))
					.poStatus(OrderStatus.PENDING.toString()).distributerId(data.getDistributerId()).build();
			em.persist(orderData);
			em.flush();
			try {
				auditRepo.save(Audit.builder().auditType(AuditType.PO_CREATED.toString()).createdBy("")
						.createdDate(new Date()).poId(orderData.getPoId()).build());
			} catch (Exception e) {
				throw new CustomException("Problem while auditing PO creation", e);
			}
			for (PoLineItemAddDTO d : data.getLineItem()) {

				poLineItemsRepository
						.save(PoLineItems.builder().drugDescription(d.getDrugDescription()).drugName(d.getDrugName())
								.drugPrice(d.getDrugPrice()).drugQuantity(d.getDrugQuantity()).poId(orderData.getPoId())
								.drugId(d.getDrugId()).distributerId(data.getDistributerId()).build());
			}
			return "PO created Successfully";
		} catch (Exception e) {
			logger.error("problem occured while creating PO", e);
			throw new CustomException("problem occured while creating PO", e);
		}
	}

	/*
	 * This is used to update Purchase Order against poId
	 */
	@Transactional
	public String updatePO(PoUpdateDTO data) {
		try {
			if (data == null) {
				logger.error("Order can't be empty");
				throw new CustomException("Order can't be empty", HttpStatus.BAD_REQUEST);
			}

//			repository.save(
//					PurchaseOrder.builder().updatedBy("").updatedDate(new Date()).poStatus(data.getPoStatus()).build());

			em.createNativeQuery(
					"UPDATE purchase_order SET updated_by = ?, updated_date = ?, po_status = ?, distributer_id = ? WHERE po_id = ?")
					.setParameter(1, "").setParameter(2, new Date()).setParameter(3, data.getPoStatus())
					.setParameter(4, data.getDistributerId()).setParameter(5, data.getPoId()).executeUpdate();

			try {
				auditRepo.save(Audit.builder().auditType(AuditType.PO_UPDATED.toString()).updatedBy("")
						.updatedDate(new Date()).poId(data.getPoId()).build());
			} catch (Exception e) {
				throw new CustomException("Problem while auditing PO updation", e);
			}

			for (PoLineItemUpdateDTO item : data.getUpdateLineItems()) {

				em.createNativeQuery("UPDATE po_line_items SET drug_description = ?, drug_id = ?, drug_name = ?,"
						+ " drug_price = ?, drug_quantity = ?, distributer_id = ? WHERE po_drug_id = ?")
						.setParameter(1, item.getDrugDescription()).setParameter(2, item.getDrugId())
						.setParameter(3, item.getDrugName()).setParameter(4, item.getDrugPrice())
						.setParameter(5, item.getDrugQuantity()).setParameter(6, data.getDistributerId())
						.setParameter(7, item.getPoDrugId()).executeUpdate();
			}

			return "PO updated Successfully";
		} catch (Exception e) {
			logger.error("problem occured while updating PO", e);
			throw new CustomException("problem occured while updating PO", e);
		}
	}

	/*
	 * It will return all created purchase order. It also support sorting,searching
	 * and pagination. Allowed search criteria are (poId, createdDate, updatedDate,
	 * createdBy, updatedBy, orderStatus). Allowed sort criteria are (createdDate,
	 * updatedDate) and sort order include(ASC, DESC).
	 */
	public List<PurchaseOrderResponse> findAllOrder(String filter, PageRequest pageRequest) {

		List<PurchaseOrder> orderInfo = new ArrayList<PurchaseOrder>();
		if (filter == null || filter.isEmpty()) {
			orderInfo = repository.findAll(pageRequest).getContent();
		} else {
			orderInfo = repository.findAll(toPredicate(filter, QPurchaseOrder.purchaseOrder), pageRequest).getContent();
		}
		if (orderInfo.isEmpty()) {
			logger.error("No purchase order available", HttpStatus.NOT_FOUND);
			throw new CustomException("No purchase order available", HttpStatus.NOT_FOUND);
		}

		List<PurchaseOrderResponse> response = new ArrayList<PurchaseOrderResponse>();
		List<PoDrugDTO> res = new ArrayList<PoDrugDTO>();
		for (PurchaseOrder data : orderInfo) {
			for (PoLineItems st : data.getPodrug()) {

				PoDrugDTO result = PoDrugDTO.builder().distributerId(st.getDistributerId())
						.drugDescription(st.getDrugDescription()).drugId(st.getDrugId()).drugName(st.getDrugName())
						.drugPrice(st.getDrugPrice()).drugQuantity(st.getDrugQuantity()).poDrugId(st.getPoDrugId())
						.poId(st.getPoId()).build();
				res.add(result);

			}
		}

		response.add(PurchaseOrderResponse.builder()
				.poData(CollectionMapper.mapPurchaseOrderToPurchaseOrderDTO(orderInfo, res)).build());

		return response;
	}

	/*
	 * Return purchase order based on id
	 */

	public PurchaseOrderDTO findPOById(Integer poId) {

		if (poId == null) {
			logger.error("Purchase order id can't be null", HttpStatus.BAD_REQUEST);
			throw new CustomException("Purchase order id can't be null", HttpStatus.BAD_REQUEST);
		}

		PurchaseOrder order = repository.findById(poId).get();
		if (order == null) {
			logger.error("No purchase order available for given id", HttpStatus.NOT_FOUND);
			throw new CustomEntityNotFoundException(PurchaseOrder.class, poId.toString());
		}

		return PurchaseOrderDTO.builder().createdBy(order.getCreatedBy()).createdDate(order.getCreatedDate())
				.poId(order.getPoId()).updatedBy(order.getUpdatedBy()).updatedDate(order.getUpdatedDate())
				.poStatus(order.getPoStatus())
				.poLineItem(order.getPodrug().stream().map(x -> new PoDrugDTO(x)).collect(Collectors.toList())).build();
	}

	/*
	 * Delete Po based on id
	 */
	public String deletePO(Integer poId, String poStatus) {
		try {
			if (poId == null) {
				logger.error("Purchase order id can't be null", HttpStatus.BAD_REQUEST);
				throw new CustomException("Purchase order id can't be null", HttpStatus.BAD_REQUEST);
			}

			if (poStatus.equals(OrderStatus.PENDING.toString())) {

				repository.deleteById(poId);

				try {
					auditRepo.save(Audit.builder().auditType(AuditType.PO_DELETED.toString()).updatedBy("")
							.updatedDate(new Date()).poId(poId).build());
				} catch (Exception e) {
					throw new CustomException("Problem while auditing PO deletion", e);
				}
			}

			return "PO deleted successfully";

		} catch (Exception e) {
			logger.error("problem occured while deleting PO", e);
			throw new CustomException("problem occured while deleting PO", e);
		}
	}
}
