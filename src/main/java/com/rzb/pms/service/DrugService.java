package com.rzb.pms.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;
import com.rzb.pms.dto.DrugAutoCompleteDTO;
import com.rzb.pms.dto.DrugDTO;
import com.rzb.pms.dto.DrugDtoReqRes;
import com.rzb.pms.dto.DrugSearchResponse;
import com.rzb.pms.exception.CustomEntityNotFoundException;
import com.rzb.pms.exception.CustomException;
import com.rzb.pms.log.Log;
import com.rzb.pms.model.Drug;
import com.rzb.pms.model.QDrug;
import com.rzb.pms.repository.DrugRepository;
import com.rzb.pms.specification.GenericSpecification;
import com.rzb.pms.specification.SearchCriteria;
import com.rzb.pms.utils.CollectionMapper;
import com.rzb.pms.utils.DrugUtil;

/**
 * @author rajib.rath
 *
 */
@Service
public class DrugService {

	@Autowired
	private DrugRepository drugRepository;

	@Log
	private Logger logger;

	@PersistenceContext
	private EntityManager em;

	/**
	 * Return all drugs
	 * 
	 * @param pageable
	 * @return List<DrugDTO>
	 */

	public List<DrugDTO> findAllDrugs(Pageable pageable) {

		Page<Drug> drugData = drugRepository.findAll(pageable);

		if (drugData.isEmpty()) {
			logger.error("No drug available", HttpStatus.NOT_FOUND);
			throw new CustomException("No drug available", HttpStatus.NOT_FOUND);
		}

		return drugData.getContent().stream().map(x -> new DrugDTO(x)).collect(Collectors.toList());
	}

	/**
	 * Return drug based on id
	 * 
	 * @param id
	 * @return drug
	 */
	public DrugDTO getdrugById(@Valid String id) {

		Optional<Drug> drugData = drugRepository.findById(id);

		if (!drugData.isPresent()) {
			logger.error("No drug found for the given id");
			throw new CustomEntityNotFoundException(Drug.class, "id", id);
		}
		Drug data = drugData.get();

		return DrugDTO.builder().brandName(data.getBrandName()).company(data.getCompany())
				.composition(data.getComposition()).drugId(data.getDrugId()).genericId(data.getGenericId())
				.genericName(data.getGenericName()).mrp(data.getMrp()).packing(data.getPacking())
				.unitPrice(data.getUnitPrice()).location(data.getLocation()).avlQntyInWhole(data.getAvlQntyInWhole())
				.avlQntyInTrimmed(data.getAvlQntyInTrimmed()).drugForm(data.getDrugForm()).build();
	}

	/**
	 * Search drug based on certain search criteria
	 * 
	 * @param search
	 * @param pageRequest
	 * @return
	 */
	public DrugSearchResponse search(String search, PageRequest pageRequest) {
		Specification<Drug> spec = null;
		if (search != null) {
			String[] queryParams = search.split(";");
			for (String queryParam : queryParams) {
				// Invoking database query by providing search Criteria
				SearchCriteria cri = DrugUtil.getCriteria(queryParam);
				if (spec == null) {
					spec = Specification.where(new GenericSpecification<Drug>(cri));
				} else {
					spec = Specification.where(spec).and(new GenericSpecification<Drug>(cri));
				}
			}
		}
		Page<Drug> drugs = drugRepository.findAll(spec, pageRequest);
		logger.info("Total Drug Record as per Search Criteria" + drugs.getContent().size());
		List<DrugAutoCompleteDTO> drugSearchList = drugs.getContent().stream().map(x -> new DrugAutoCompleteDTO(x))
				.collect(Collectors.toList());

		return new DrugSearchResponse(drugSearchList, drugs.getTotalElements());
	}

	/**
	 * Save drug info to db
	 * 
	 * @param drugData
	 * @return
	 */
	@Transactional
	public String addDrug(DrugDtoReqRes data) {

		if (data == null) {
			logger.error("Drug information can't be empty", HttpStatus.NOT_ACCEPTABLE);
			throw new CustomException("Drug information can't be empty", HttpStatus.NOT_ACCEPTABLE);
		}
		try {
			em.createNativeQuery(
					"INSERT INTO drug (drug_id, avl_qnty_in_trimmed, avl_qnty_in_whole, brand_name, company, composition,"
							+ " drug_form, expiry_date, generic_id, generic_name,location, mrp, packing, unit_price)"
							+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
					.setParameter(1, "TD" + em.createNativeQuery("select nextval ('drug_id_seq')").getSingleResult())
					.setParameter(2, data.getAvlQntyInWhole() * data.getPacking())
					.setParameter(3, data.getAvlQntyInWhole()).setParameter(4, data.getBrandName())
					.setParameter(5, data.getCompany()).setParameter(6, data.getComposition())
					.setParameter(7, data.getDrugForm()).setParameter(8, data.getExpiryDate())
					.setParameter(9, data.getGenericId()).setParameter(10, data.getGenericName())
					.setParameter(11, data.getLocation()).setParameter(12, data.getMrp())
					.setParameter(13, data.getPacking()).setParameter(14, data.getMrp() / data.getPacking())
					.executeUpdate();

			return "Drug info saved successfully";
		} catch (Exception e) {
			logger.error("Problem while adding drug, Please try again", e);
			throw new CustomException("Drug information can't be empty", e);
		}

	}

	/**
	 * return drugs by genericId
	 * 
	 * @param genericId
	 * @return
	 */
	public List<DrugDTO> getDrugByGenericId(String genericId, Pageable page) {
		if (genericId == null) {
			logger.error("Generic Id can't be null", HttpStatus.BAD_REQUEST);
			throw new CustomException("Generic Id can't be null", HttpStatus.BAD_REQUEST);
		}
		final QDrug drugs = QDrug.drug;
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(drugs.genericId.eq(genericId));
		Page<Drug> data = drugRepository.findAll(builder.getValue(), page);
		if (data == null) {
			logger.error("Drug Details not found for the given generic Id", HttpStatus.NOT_FOUND);
			throw new CustomEntityNotFoundException(Drug.class, genericId);
		}

		return CollectionMapper.mapDrugDtoReqResReqRes(data.getContent());

	}

	/**
	 * return drugs by genericName
	 * 
	 * @param genericId
	 * @return
	 */
	public List<DrugDTO> getDrugByGenericName(String name, Pageable page) {
		if (name == null) {
			logger.error("Generic name can't be null", HttpStatus.BAD_REQUEST);
			throw new CustomException("Generic name can't be null", HttpStatus.BAD_REQUEST);
		}

		final QDrug drugs = QDrug.drug;
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(drugs.genericName.eq(name));
		Page<Drug> data = drugRepository.findAll(builder.getValue(), page);
		if (data == null) {
			logger.error("Drug Details not found for the given generic name", HttpStatus.NOT_FOUND);
			throw new CustomEntityNotFoundException(Drug.class, name);
		}

		return data.getContent().stream().map(x -> new DrugDTO(x)).collect(Collectors.toList());

	}

	/**
	 * return drugs by composition used to get substitute for drug with same salt
	 * 
	 * @param genericId
	 * @return
	 */
	public List<DrugDTO> getDrugByComposition(String composition, Pageable page) {
		if (composition == null) {
			logger.error("composition can't be null", HttpStatus.BAD_REQUEST);
			throw new CustomException("composition can't be null", HttpStatus.BAD_REQUEST);
		}

		final QDrug drugs = QDrug.drug;
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(drugs.composition.contains(composition));
		Page<Drug> data = drugRepository.findAll(builder.getValue(), page);
		if (data == null) {
			logger.error("Drug Details not found for the given composition", HttpStatus.NOT_FOUND);
			throw new CustomEntityNotFoundException(Drug.class, composition);
		}

		return data.getContent().stream().map(x -> new DrugDTO(x)).collect(Collectors.toList());

	}

	/**
	 * update drug by id
	 * 
	 * @param drugData
	 * @param drugId
	 * @return
	 */
	@Transactional
	public String updateDrugData(DrugDtoReqRes drugData, String drugId) {

		if (drugId == null) {
			logger.error("Drug Id can't be null", HttpStatus.BAD_REQUEST);
			throw new CustomException("Drug Id can't be null", HttpStatus.BAD_REQUEST);
		}
		Drug data = drugRepository.findById(drugId).get();
		if (data == null) {
			logger.error("Drug Details not found", HttpStatus.NOT_FOUND);
			throw new CustomEntityNotFoundException(Drug.class, drugId);
		}
		try {
			drugRepository.save(Drug.builder().brandName(drugData.getBrandName()).company(drugData.getCompany())
					.composition(drugData.getComposition()).drugForm(drugData.getDrugForm())
					.expiryDate(drugData.getExpiryDate()).genericId(drugData.getGenericId())
					.genericName(drugData.getGenericName()).location(drugData.getLocation()).mrp(drugData.getMrp())
					.avlQntyInTrimmed(drugData.getAvlQntyInTrimmed()).avlQntyInWhole(drugData.getAvlQntyInWhole())
					.packing(drugData.getPacking()).build());

			return "Drug info updated successfully";
		} catch (Exception e) {
			logger.error("Problem while adding drug, Please try again", e.getCause());
			throw new CustomException("Drug information can't be empty", e.getCause());
		}

	}

}
