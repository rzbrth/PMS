package com.rzb.pms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rzb.pms.model.DrugPharma;
import com.rzb.pms.model.GenericPharma;
import com.rzb.pms.repository.DrugPharmaRepository;
import com.rzb.pms.repository.GenericPharmaRepository;

@Service
public class MapperService {
	@Autowired
	private GenericPharmaRepository genericPharmaRepository;

	@Autowired
	private DrugPharmaRepository drugPharmaRepository;

	public String mapData() {

		List<DrugPharma> drugData = drugPharmaRepository.findAll();
		List<GenericPharma> genericData = genericPharmaRepository.findAll();

		for (GenericPharma data : genericData) {
			for (DrugPharma drugs : drugData) {
				String input = data.getName(); 
				boolean isPresent = input.indexOf(drugs.getComposition()) != -1 ? true : false;
				if (isPresent) {

					drugs.setGenericId(data.getId());
					drugPharmaRepository.save(drugs);
				}

			}
		}

		return "success";

	}

}
