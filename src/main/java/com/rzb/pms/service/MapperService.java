package com.rzb.pms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rzb.pms.model.Drug;
import com.rzb.pms.model.Generic;
import com.rzb.pms.repository.DrugRepository;
import com.rzb.pms.repository.GenericRepository;

@Service
public class MapperService {
	@Autowired
	private GenericRepository genericRepository;

	@Autowired
	private DrugRepository drugRepository;

	public String mapData() {

		List<Drug> drugData = drugRepository.findAll();
		List<Generic> genericData = genericRepository.findAll();

		for (Generic data : genericData) {
			for (Drug drugs : drugData) {
				String input = data.getName(); 
				boolean isPresent = input.indexOf(drugs.getComposition()) != -1 ? true : false;
				if (isPresent) {

					drugs.setGenericId(data.getGenericId());
					drugRepository.save(drugs);
				}

			}
		}

		return "success";

	}

}
