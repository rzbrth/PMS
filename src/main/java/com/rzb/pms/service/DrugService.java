package com.rzb.pms.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
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
import com.rzb.pms.repository.GenericRepository;
import com.rzb.pms.specification.GenericSpecification;
import com.rzb.pms.specification.SearchCriteria;
import com.rzb.pms.specification.SearchKey;
import com.rzb.pms.specification.SearchOperators;
import com.rzb.pms.utils.CollectionMapper;

/**
 * @author rajib.rath
 *
 */
@Service
public class DrugService {

	@Autowired
	private DrugRepository drugRepository;

	@Autowired
	private GenericRepository genericRepository;

	@Log
	private Logger logger;

	@PersistenceContext
	private EntityManager em;

	private Drug drug;

	private CollectionMapper mapper;

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
				.avlQntyInTrimmed(data.getAvlQntyInTrimmed()).drugForm(data.getDrugForm())

				.build();
	}

	/**
	 * This method will take the sort criteria and process it accordingly Only
	 * Supported sort criteria are expiryDate, unitPrice, mrp and sort orders are
	 * ASC(Ascending) or DSC(Descending)
	 * 
	 * @param sort
	 * @return sortObj
	 */
	public Sort getSortObject(String sort) {
		Sort sortObj = null;
		String[] sorts = sort.split(",");
		for (String s : sorts) {
			if (s.split(":").length != 2) {
				logger.error("Please give a proper sort argument", HttpStatus.NOT_FOUND);
				throw new CustomException("Please give a proper sort argument", HttpStatus.NOT_FOUND);
			}
			String sortBy = s.split(":")[0];
			String sortOrder = s.split(":")[1];

			if (sortBy.equalsIgnoreCase("expiryDate") || sortBy.equalsIgnoreCase("unitPrice")
					|| sortBy.equalsIgnoreCase("mrp")) {
				if ("ASC".equals(sortOrder) || "DESC".equals(sortOrder)) {
					if (sortObj == null) {
						sortObj = Sort.by(Direction.fromString(sortOrder), sortBy);
					} else {
						// sortObj = sortObj.and(new Sort(Direction.fromString(sortOrder), sortBy));
						sortObj = sortObj.and(Sort.by(Direction.fromString(sortOrder), sortBy));
					}
				} else {
					logger.error("Please give a proper sort Order like ASC(Ascending) or DSC(Descending) ",
							HttpStatus.NOT_FOUND);
					throw new CustomException("Please give a proper sort Order like ASC(Ascending) or DSC(Descending) ",
							HttpStatus.NOT_FOUND);

				}
			} else {
				logger.error("Please give a proper sort argument Like expiryDate, unitPrice, mrp",
						HttpStatus.NOT_FOUND);
				throw new CustomException("Please give a proper sort argument Like expiryDate, unitPrice, mrp",
						HttpStatus.NOT_FOUND);
			}
		}
		return sortObj;
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
				SearchCriteria cri = getCriteria(queryParam);
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

	/*
	 * This method will take custom queryParam of the form (Key Operator Value) eg:
	 * company==cipla . queryParamwill be processed into SearchCriteria members like
	 * key(company), Operation(==), Value(cipla) based on the Search Key and
	 * Operator. Currently supported Search keys are genericanme, brandName,
	 * company, composition, location and Supported Search operators are ==, >=,=lk=
	 * ,<
	 */
	public SearchCriteria getCriteria(String queryParam) {
		String[] o = queryParam.split("==|>=|=lk=|<");
		if (o.length < 2) {
			logger.error("Please provide proper search Criteria, Supported operators are ==, >=,=lk= ,< ",
					HttpStatus.BAD_REQUEST);
			throw new CustomException("Please provide proper search Criteria, Supported operators are ==, >=,=lk= ,< ",
					HttpStatus.BAD_REQUEST);
		}
		String operator = queryParam.substring(o[0].length(), queryParam.length() - o[1].length()).replaceAll("\\s+",
				"");
		Object value = o[1].trim().replaceAll("\\s+", " ");
		String key = o[0].replaceAll("\\s+", "");

		if (key.equalsIgnoreCase("name")) {
			return new SearchCriteria(key, operator, value);
		}

		switch (SearchKey.getKeyData(key)) {

		case DRUG_NAME: {
			if (!(SearchOperators.LIKE.getName().equals(operator)
					|| SearchOperators.EQUALITY.getName().equals(operator))) {
				logger.error("Please provide proper search params, brandName will only support =lk= OR ==",
						HttpStatus.BAD_REQUEST);
				throw new CustomException("Please provide proper search params, brandName will only support =lk= OR ==",
						HttpStatus.BAD_REQUEST);
			} else {
				return new SearchCriteria(key, operator, value);
			}
		}

		case GENERIC_NAME: {
			if (!(SearchOperators.LIKE.getName().equals(operator)
					|| SearchOperators.EQUALITY.getName().equals(operator))) {
				logger.error("Please provide proper search params, genericName will only support =lk= OR ==",
						HttpStatus.BAD_REQUEST);
				throw new CustomException(
						"Please provide proper search params, genericName will only support =lk= OR ==",
						HttpStatus.BAD_REQUEST);
			} else {
				return new SearchCriteria(key, operator, value);
			}
		}

		case COMPANY: {
			if (!(SearchOperators.LIKE.getName().equals(operator)
					|| SearchOperators.EQUALITY.getName().equals(operator))) {
				logger.error("Please provide proper search params, company will only support =lk= OR ==",
						HttpStatus.BAD_REQUEST);
				throw new CustomException("Please provide proper search params, company will only support =lk= OR ==",
						HttpStatus.BAD_REQUEST);
			} else {
				return new SearchCriteria(key, operator, value);
			}
		}
		case COMPOSITION: {
			if (!(SearchOperators.LIKE.getName().equals(operator)
					|| SearchOperators.EQUALITY.getName().equals(operator))) {
				logger.error("Please provide proper search params, composition will only support =lk= OR ==",
						HttpStatus.BAD_REQUEST);
				throw new CustomException(
						"Please provide proper search params, composition will only support =lk= OR ==",
						HttpStatus.BAD_REQUEST);
			} else {
				return new SearchCriteria(key, operator, value);
			}
		}

		case LOCATION: {
			if (!(SearchOperators.LIKE.getName().equals(operator)
					|| SearchOperators.EQUALITY.getName().equals(operator))) {
				logger.error("Please provide proper search params, location will only support =lk= OR ==",
						HttpStatus.BAD_REQUEST);
				throw new CustomException("Please provide proper search params, location will only support =lk= OR ==",
						HttpStatus.BAD_REQUEST);
			} else {
				return new SearchCriteria(key, operator, value);
			}
		}

		default:
			logger.error(
					"Please Provide right search parameter . Like genericanme, brandName, company, composition, location",
					HttpStatus.BAD_REQUEST);
			throw new CustomException(
					"Please Provide right search parameter . Like genericanme, brandName, company, composition, location",
					HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Save drug info to db
	 * 
	 * @param drugData
	 * @return
	 */
	public String addDrug(DrugDtoReqRes drugData) {

		if (drugData == null) {
			logger.error("Drug information can't be empty", HttpStatus.NOT_ACCEPTABLE);
			throw new CustomException("Drug information can't be empty", HttpStatus.NOT_ACCEPTABLE);
		}
		try {
			drugRepository.save(Drug.builder().brandName(drugData.getBrandName()).company(drugData.getCompany())
					.composition(drugData.getComposition()).drugForm(drugData.getDrugForm())
					.expiryDate(drugData.getExpiryDate()).genericId(drugData.getGenericId())
					.genericName(drugData.getGenericName()).location(drugData.getLocation()).mrp(drugData.getMrp())
					.avlQntyInTrimmed(drugData.getAvlQntyInTrimmed()).avlQntyInWhole(drugData.getAvlQntyInWhole())
					.packing(drugData.getPacking()).build());

			return "Drug info saved successfully";
		} catch (Exception e) {
			logger.error("Problem while adding drug, Please try again", e.getCause());
			throw new CustomException("Drug information can't be empty", e.getCause());
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

		final JPAQuery<Drug> query = new JPAQuery<Drug>(em);
		final QDrug drugs = QDrug.drug;
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(drugs.genericId.eq(genericId));
		Page<Drug> data = drugRepository.findAll(builder.getValue(), page);
		if (data == null) {
			logger.error("Drug Details not found for the given generic Id", HttpStatus.NOT_FOUND);
			throw new CustomEntityNotFoundException(Drug.class, genericId);
		}

		return mapper.mapDrugDtoReqResReqRes(data.getContent());

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

		final JPAQuery<Drug> query = new JPAQuery<Drug>(em);
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
	 * return drugs by composition
	 * used to get substitute for  drug with same salt 
	 * 
	 * @param genericId
	 * @return
	 */
	public List<DrugDTO> getDrugByComposition(String composition, Pageable page) {
		if (composition == null) {
			logger.error("composition can't be null", HttpStatus.BAD_REQUEST);
			throw new CustomException("composition can't be null", HttpStatus.BAD_REQUEST);
		}

		final JPAQuery<Drug> query = new JPAQuery<Drug>(em);
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
