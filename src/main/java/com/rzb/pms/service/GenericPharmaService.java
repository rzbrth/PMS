package com.rzb.pms.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.rzb.pms.exception.CustomEntityNotFoundException;
import com.rzb.pms.model.GenericPharma;
import com.rzb.pms.repository.GenericPharmaRepository;

@Service
public class GenericPharmaService {
	 
	private static final Logger log = LoggerFactory.getLogger(GenericPharmaService.class);


	@Autowired
	private GenericPharmaRepository genericPharmaRepository;

	public List<GenericPharma> findAllGenerics() {

		return genericPharmaRepository.findAll();

	}
	
	public Optional<GenericPharma> getGenericById(String id) {
		
		if(id.isEmpty()) {
			log.error("Id can not be empty", HttpStatus.NOT_FOUND);
			throw new CustomEntityNotFoundException(GenericPharma.class, "id",id);
		}
		Optional<GenericPharma> genData = genericPharmaRepository.findById(id);
		if(!genData.isPresent()) {
			log.error("No generic found for the given id", HttpStatus.NOT_FOUND);
			throw new CustomEntityNotFoundException(GenericPharma.class, "id",id);
		}
		
		return genData;
		
	}
}
