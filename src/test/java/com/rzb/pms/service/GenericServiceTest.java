package com.rzb.pms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;

import com.rzb.pms.dto.GenericDto;
import com.rzb.pms.model.Generic;
import com.rzb.pms.repository.GenericRepository;

@RunWith(MockitoJUnitRunner.class)
public class GenericServiceTest {

	@Autowired
	private GenericService service;

	@MockBean(name = "genericRepository")
	private GenericRepository genericRepository;

	private Pageable pageable;

//	@Test
//	void findAllGenericsTest() {
//		Generic generic = Generic.builder().name("aminosalicylic acid").genericId("GEN3635").build();
//		List<Generic> expectedData = Arrays.asList(generic);
//		List<Generic> actualData = genericRepository.findAll();
//
//		when(actualData).thenReturn(expectedData);
//		assertThat(actualData).isEqualTo(expectedData);
//	}

	@Test
	public void getGenericByIdTest() {

		String genericId = "GEN3635";
		Generic expectedData = Generic.builder().name("aminosalicylic acid").genericId(genericId).build();
		when(genericRepository.findByGenericId(genericId)).thenReturn(expectedData);
		
				
		assertEquals(GenericDto.builder().name("aminosalicylic acid").genericId(genericId).build(), service.getGenericById(genericId));

		//verify(genericRepository).count();
	}
}
