package com.rzb.pms.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.querydsl.core.BooleanBuilder;
import com.rzb.pms.dto.DrugDTO;
import com.rzb.pms.dto.DrugDtoReqRes;
import com.rzb.pms.dto.DrugSearchResponse;
import com.rzb.pms.model.Audit;
import com.rzb.pms.model.Drug;
import com.rzb.pms.model.QDrug;
import com.rzb.pms.model.enums.AuditType;
import com.rzb.pms.model.enums.ReportCategory;
import com.rzb.pms.repository.AuditRepository;
import com.rzb.pms.repository.DistributerRepository;
import com.rzb.pms.repository.DrugRepository;
import com.rzb.pms.repository.StockRepository;
import com.rzb.pms.rsql.SearchCriteria;
import com.rzb.pms.rsql.jpa.GenericSpecification;
import com.rzb.pms.utils.BaseUtil;
import com.rzb.pms.utils.CollectionMapper;
import com.rzb.pms.utils.ReportUtills;

import lombok.extern.slf4j.Slf4j;

/**
 * @author rajib.rath
 * @param <K>
 *
 */
@Service
@SuppressWarnings(value = { "unchecked", "unused", "rawtypes" })
@Slf4j
public class DrugService<K> {

	@Autowired
	private DrugRepository drugRepository;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private DistributerRepository repository;

	@Autowired
	private AuditRepository auditRepo;

	private ReportUtills report = new ReportUtills();

	private BooleanBuilder builder = new BooleanBuilder();

	/**
	 * Return all drugs
	 * 
	 * @param pageable
	 * @param isExported
	 * @return List<DrugDTO>
	 */

	public List<K> findAllDrugs(Pageable pageable, Boolean isExported, String exportType,
			HttpServletResponse response) {

		Page<Drug> drugData = drugRepository.findAll(pageable);

		if (drugData.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No drug available");
		}

		if (isExported) {

			try {
				List<K> data = (List<K>) report.generateReport(response, exportType, ReportCategory.ALL_DRUG.toString(),
						drugData.getContent().stream().map(x -> new DrugDTO(x)).collect(Collectors.toList()));
				return data;
			} catch (Exception e) {
				log.error("Problem while exporting Drug info to : {}", exportType, e);
			}
		}

		return (List<K>) drugData.getContent().stream().map(x -> new DrugDTO(x)).collect(Collectors.toList());
	}

	/**
	 * Return drug based on id
	 * 
	 * @param id
	 * @return drug
	 */
	public DrugDTO getdrugById(@Valid String id) {
		Drug data = drugRepository.findById(id).orElse(null);

		if (data == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Drug Not Found");
		}
		return DrugDTO.builder().brandName(data.getBrandName()).company(data.getCompany())
				.composition(data.getComposition()).drugId(data.getDrugId()).genericId(data.getGenericId())
				.genericName(data.getGenericName()).mrp(data.getMrp()).packing(data.getPacking())
				.unitPrice(data.getUnitPrice())
				.location(String.join(",", stockRepository.findLocationByDrugId(data.getDrugId())))
				.drugForm(data.getDrugForm()).build();
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
		List<Drug> drugs = null;

		if (search != null) {
			String[] queryParams = search.split(";");
			for (String queryParam : queryParams) {
				// Invoking database query by providing search Criteria
				SearchCriteria cri = BaseUtil.getCriteria(queryParam);
				if (spec == null) {
					spec = Specification.where(new GenericSpecification<Drug>(cri));
				} else {
					spec = Specification.where(spec).and(new GenericSpecification<Drug>(cri));
				}
			}
			drugs = drugRepository.findAll(spec, pageRequest).getContent();
		} else {
			drugs = drugRepository.findAll(pageRequest).getContent();
		}

		if (drugs.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No data available");

		}
		log.info("Total Drug Record as per Search Criteria : {}", drugs.size());

		return new DrugSearchResponse(CollectionMapper.mapDrugToDrugAutoCompleteDTO(drugs, stockRepository),
				drugs.size());
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
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Drug info can't be empty");
		}
		try {
			Query q = em
					.createNativeQuery("INSERT INTO drug (drug_id, brand_name, company, composition,"
							+ " drug_form, expiry_date, generic_id, generic_name, mrp, packing, unit_price)"
							+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
					.setParameter(1, "TD" + em.createNativeQuery("select nextval ('drug_id_seq')").getSingleResult())
					.setParameter(2, data.getBrandName()).setParameter(3, data.getCompany())
					.setParameter(4, data.getComposition()).setParameter(5, data.getDrugForm())
					.setParameter(6, data.getExpiryDate()).setParameter(7, data.getGenericId())
					.setParameter(8, data.getGenericName()).setParameter(9, data.getMrp())
					.setParameter(10, data.getPacking()).setParameter(11, data.getMrp() / data.getPacking());
			q.executeUpdate();
			String drugId = (String) q.getSingleResult();

			// Auditing
			try {
				auditRepo.save(Audit.builder().auditType(AuditType.DRUG_CREATED.toString())
						.createdBy(BaseUtil.getLoggedInuserName()).createdDate(LocalDate.now()).drugId(drugId).build());
			} catch (Exception e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Problem while auditing drug info",
						e);
			}
			return "Drug info saved successfully";
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Problem occurred while saving drug info", e);
		}

	}

	/**
	 * return drugs by genericId
	 * 
	 * @param genericId
	 * @return
	 */
	/*
	 * public List<DrugDTO> getDrugByGenericId(String genericId, Pageable page) { if
	 * (genericId == null) { log.error("Generic Id can't be null",
	 * HttpStatus.BAD_REQUEST); throw new
	 * CustomException("Generic Id can't be null", HttpStatus.BAD_REQUEST); } final
	 * QDrug drugs = QDrug.drug; builder.and(drugs.genericId.eq(genericId));
	 * Page<Drug> data = drugRepository.findAll(builder.getValue(), page); if (data
	 * == null) { log.error("Drug Details not found for the given generic Id",
	 * HttpStatus.NOT_FOUND); throw new CustomEntityNotFoundException(Drug.class,
	 * "genericId", genericId); }
	 * 
	 * return CollectionMapper.mapDrugDtoDrugDTO(data.getContent(),
	 * stockRepository);
	 * 
	 * }
	 */

	/**
	 * return drugs by genericName
	 * 
	 * @param genericId
	 * @return
	 */
	public List<DrugDTO> getDrugByGenericName(String name, Pageable page) {
		if (name == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Generic name can't be null");

		}

		final QDrug drugs = QDrug.drug;
		builder.and(drugs.genericName.eq(name));
		Page<Drug> data = drugRepository.findAll(builder.getValue(), page);
		if (data == null) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT,
					"Drug Details not found for the given generic name");
		}

		return data.getContent().stream().map(x -> new DrugDTO(x)).collect(Collectors.toList());

	}

	/**
	 * return drugs by composition used to get substitute for drug with same salt
	 * 
	 * @param genericId
	 * @return
	 */
//	public List<DrugDTO> getDrugByComposition(String composition, Pageable page) {
//		if (composition == null) {
//			log.error("composition can't be null", HttpStatus.BAD_REQUEST);
//			throw new CustomException("composition can't be null", HttpStatus.BAD_REQUEST);
//		}
//
//		try {
//			List<Drug> data = drugRepository.findAll(toPredicate(composition, QDrug.drug), page).getContent();
//
//			if (data.isEmpty()) {
//				log.error("Drug Details not found for the given composition", HttpStatus.NOT_FOUND);
//				throw new CustomEntityNotFoundException(Drug.class, "composition", composition);
//			}
//
//			return data.stream().map(x -> new DrugDTO(x)).collect(Collectors.toList());
//		} catch (Exception e) {
//			throw new CustomException("Exception while parsing", e);
//		}
//	}

	public List<DrugDTO> getDrugByComposition(String composition, Pageable page) {

		try {
			List<Drug> data = drugRepository.findSubstitute(composition, page.getPageSize());

			if (data.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NO_CONTENT,
						"Drug Details not found for the given composition");
			}

			return data.stream().map(x -> new DrugDTO(x)).collect(Collectors.toList());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Exception while finding alternate drug", e);
		}
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
		Drug data = null;
		if (drugId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Drug id can't be null");
		}
		Drug res = drugRepository.findById(drugId).orElse(data);
		if (res == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Drug details not found");

		}
		try {
			Drug record = Drug.builder().brandName(drugData.getBrandName()).company(drugData.getCompany())
					.composition(drugData.getComposition()).drugForm(drugData.getDrugForm())
					.expiryDate(drugData.getExpiryDate()).genericId(drugData.getGenericId())
					.genericName(drugData.getGenericName()).mrp(drugData.getMrp()).packing(drugData.getPacking())
					.build();
			drugRepository.saveAndFlush(record);
			// Auditing
			try {
				auditRepo.save(Audit.builder().auditType(AuditType.DRUG_UPDATED.toString())
						.updatedBy(BaseUtil.getLoggedInuserName()).updatedDate(LocalDate.now())
						.drugId(record.getDrugId()).build());
			} catch (Exception e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Problem while auditing drug info",
						e);

			}
			return "Drug info updated successfully";
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Drug information can't be empty", e);
		}

	}

	public DrugDTO getdrugByName(@Valid String brandName) {

		if (brandName == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brand name can't be null");
		}
		DrugDTO drugData = drugRepository.findByBrandName(brandName);

		if (drugData == null) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Drug Details not found for the given drug name");
		}
		return drugRepository.findByBrandName(brandName);
	}

}
