package com.rzb.pms.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;
import com.rzb.pms.dto.AuditType;
import com.rzb.pms.dto.DrugDTO;
import com.rzb.pms.dto.DrugDtoReqRes;
import com.rzb.pms.dto.DrugSearchResponse;
import com.rzb.pms.dto.ReportCategory;
import com.rzb.pms.exception.CustomEntityNotFoundException;
import com.rzb.pms.exception.CustomException;
import com.rzb.pms.model.Audit;
import com.rzb.pms.model.Drug;
import com.rzb.pms.model.QDrug;
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
			throw new CustomException("No drug available", HttpStatus.NO_CONTENT);
		}

		if (isExported) {

			try {
				List<K> data = (List<K>) report.generateReport(response, exportType, ReportCategory.ALL_DRUG.toString(),
						drugData.getContent().stream().map(x -> new DrugDTO(x)).collect(Collectors.toList()));
				return data;
			} catch (Exception e) {
				throw new CustomException("Problem while exporting Drug info to :--<< (" + exportType
						+ ")-->> \"+\"\\t\"+\"exception occured-->", e);
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

		Optional<Drug> drugData = drugRepository.findById(id);

		if (!drugData.isPresent()) {
			throw new CustomEntityNotFoundException(Drug.class, "drugId", id);
		}
		Drug data = drugData.get();
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
		}
		Page<Drug> drugs = drugRepository.findAll(spec, pageRequest);
		if (drugs.isEmpty()) {
			throw new CustomException("No data available", HttpStatus.NO_CONTENT);
		}
		log.info("Total Drug Record as per Search Criteria" + drugs.getContent().size());

		return new DrugSearchResponse(
				CollectionMapper.mapDrugToDrugAutoCompleteDTO(drugs.getContent(), stockRepository),
				drugs.getTotalElements());
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
			log.error("Drug information can't be empty", HttpStatus.BAD_REQUEST);
			throw new CustomException("Drug information can't be empty", HttpStatus.BAD_REQUEST);
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

			try {
				auditRepo.save(Audit.builder().auditType(AuditType.DRUG_CREATED.toString()).createdBy("")
						.createdDate(LocalDate.now()).drugId(drugId).build());
			} catch (Exception e) {
				throw new CustomException("Problem while auditing drug info", e);
			}
			return "Drug info saved successfully";
		} catch (Exception e) {
			throw new CustomException("Drug information can't be empty", e);
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
			log.error("Generic name can't be null", HttpStatus.BAD_REQUEST);
			throw new CustomException("Generic name can't be null", HttpStatus.BAD_REQUEST);
		}

		final QDrug drugs = QDrug.drug;
		builder.and(drugs.genericName.eq(name));
		Page<Drug> data = drugRepository.findAll(builder.getValue(), page);
		if (data == null) {
			log.error("Drug Details not found for the given generic name", HttpStatus.NO_CONTENT);
			throw new CustomEntityNotFoundException(Drug.class, "drugName", name);
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
			log.error("composition can't be null", HttpStatus.BAD_REQUEST);
			throw new CustomException("composition can't be null", HttpStatus.BAD_REQUEST);
		}

		final QDrug drugs = QDrug.drug;
		builder.and(drugs.composition.contains(composition));
		Page<Drug> data = drugRepository.findAll(builder.getValue(), page);
		if (data == null) {
			log.error("Drug Details not found for the given composition", HttpStatus.NOT_FOUND);
			throw new CustomEntityNotFoundException(Drug.class, "composition", composition);
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
			log.error("Drug Id can't be null", HttpStatus.BAD_REQUEST);
			throw new CustomException("Drug Id can't be null", HttpStatus.BAD_REQUEST);
		}
		Drug data = drugRepository.findById(drugId).get();
		if (data == null) {
			log.error("Drug Details not found", HttpStatus.NOT_FOUND);
			throw new CustomEntityNotFoundException(Drug.class, "drugId", drugId);
		}
		try {
			Drug record = Drug.builder().brandName(drugData.getBrandName()).company(drugData.getCompany())
					.composition(drugData.getComposition()).drugForm(drugData.getDrugForm())
					.expiryDate(drugData.getExpiryDate()).genericId(drugData.getGenericId())
					.genericName(drugData.getGenericName()).mrp(drugData.getMrp()).packing(drugData.getPacking())
					.build();
			drugRepository.saveAndFlush(record);
			try {
				auditRepo.save(Audit.builder().auditType(AuditType.DRUG_UPDATED.toString()).updatedBy("")
						.updatedDate(LocalDate.now()).drugId(record.getDrugId()).build());
			} catch (Exception e) {
				throw new CustomException("Problem while auditing drug info", e);
			}
			return "Drug info updated successfully";
		} catch (Exception e) {
			log.error("Problem while adding drug, Please try again", e.getCause());
			throw new CustomException("Drug information can't be empty", e.getCause());
		}

	}

	public DrugDTO getdrugByName(@Valid String brandName) {

		if (brandName == null) {
			log.error("Brand name can't be null", HttpStatus.BAD_REQUEST);
		}

		return drugRepository.findByBrandName(brandName);
	}

}
