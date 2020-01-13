package com.rzb.pms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.rzb.pms.repository.GenericRepository;
import com.rzb.pms.service.GenericService;

@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
public class GenericControllerTest {

	@Autowired
	private GenericService genericService;
	
	@MockBean
	private GenericRepository genericRepository;
	
	
	
	
}
