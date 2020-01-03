package com.rzb.pms.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rzb.pms.log.Log;
import com.rzb.pms.repository.DrugRepository;
import com.rzb.pms.repository.GenericRepository;

@Service
public class AutoCompleteService {

	@Log
	private Logger logger;
	
	@Autowired
	private GenericRepository genericRepository;
	
	@Autowired
	private DrugRepository drugRepository;
	
	
	
	
}
