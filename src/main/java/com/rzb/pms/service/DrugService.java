package com.rzb.pms.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.rzb.pms.exception.CustomEntityNotFoundException;
import com.rzb.pms.model.Drugs;
import com.rzb.pms.repository.DrugRepository;

@Service
public class DrugService {

	Logger logger = LoggerFactory.getLogger(DrugService.class);
	@Autowired
	private DrugRepository drugRepository;

	public List<Drugs> getAllDrugs() {

		List<Drugs> drugs = drugRepository.findAll();

		if (drugs.isEmpty()) {
			logger.warn("No Drug Available", HttpStatus.NOT_FOUND);
			throw new CustomEntityNotFoundException(Drugs.class, "id", drugs.get(0).getDrugCode());
		}
		return drugs;

	}

	public Optional<Drugs> getDrugById(String drugId) {

		if (drugId.isEmpty()) {
			logger.warn("Id can not be empty", HttpStatus.NOT_FOUND);
			//throw new CustomEntityNotFoundException(Drugs.class, "id", drugId);
			throw new CustomEntityNotFoundException(Drugs.class,drugId);

		}
		Optional<Drugs> drugs = drugRepository.findById(drugId);

		if (!drugs.isPresent()) {
			logger.warn("No Drug Available for given id", HttpStatus.NOT_FOUND);
			throw new CustomEntityNotFoundException(Drugs.class, "id", drugId);
		}

		return drugs;

	}
}
