package com.rzb.pms.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.rzb.pms.exception.CustomEntityNotFoundException;
import com.rzb.pms.model.Generics;
import com.rzb.pms.repository.GenericRepository;

@Service
public class GenericService {

	Logger logger = LoggerFactory.getLogger(GenericService.class);
	@Autowired
	private GenericRepository genericRepository;

	public List<Generics> getAllGenerics() {

		List<Generics> generics = genericRepository.findAll();

		if (generics.isEmpty()) {
			logger.warn("No Generic Available", HttpStatus.NOT_FOUND);
			throw new CustomEntityNotFoundException(Generics.class, "id", generics.get(0).getCode());
		}
		return generics;

	}
}