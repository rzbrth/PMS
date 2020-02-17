package com.rzb.pms.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.rzb.pms.dto.GenericDto;
import com.rzb.pms.dto.GenericResponseByName;
import com.rzb.pms.exception.CustomEntityNotFoundException;
import com.rzb.pms.exception.CustomException;
import com.rzb.pms.log.Log;
import com.rzb.pms.model.Generic;
import com.rzb.pms.repository.GenericRepository;

@Service
public class GenericService {

	/*
	 * @Log private Logger log;
	 * 
	 * private final GenericRepository genericRepository;
	 * 
	 * @Autowired public GenericService(GenericRepository genericRepository) {
	 * this.genericRepository = genericRepository; }
	 * 
	 * public List<GenericDto> findAllGenerics(Pageable pageable) {
	 * 
	 * Page<Generic> data = genericRepository.findAll(pageable);
	 * 
	 * if (data.isEmpty()) { log.error("No data found"); throw new
	 * CustomException("No data found", HttpStatus.NOT_FOUND); } List<GenericDto>
	 * genericData = data.getContent().stream().map(x -> new GenericDto(x))
	 * .collect(Collectors.toList());
	 * 
	 * return genericData;
	 * 
	 * }
	 * 
	 * public GenericDto getGenericById(String id) {
	 * 
	 * Optional<Generic> genData = genericRepository.findById(id); if
	 * (!genData.isPresent()) { log.error("No generic found for the given id",
	 * HttpStatus.NOT_FOUND); throw new CustomEntityNotFoundException(Generic.class,
	 * "genericId", id); }
	 * 
	 * return
	 * GenericDto.builder().genericId(genData.get().getGenericId()).name(genData.get
	 * ().getName()) .drugs(genData.get().getDrugs()).build();
	 * 
	 * }
	 * 
	 * public GenericResponseByName getGenericByName(String name) {
	 * 
	 * if (name == null) { log.error("Generic name can't be null"); throw new
	 * CustomException("Generic name can't be null", HttpStatus.BAD_REQUEST); }
	 * 
	 * Generic generic = genericRepository.findByNameLike(name);
	 * 
	 * if (generic == null) { log.error("No data found for given generic name");
	 * throw new CustomException("No data found for given generic name",
	 * HttpStatus.NOT_FOUND); }
	 * 
	 * return
	 * GenericResponseByName.builder().name(generic.getName()).genericId(generic.
	 * getGenericId()).build(); }
	 */
}
