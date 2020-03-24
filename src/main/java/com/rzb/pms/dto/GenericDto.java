package com.rzb.pms.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.rzb.pms.model.Drug;
import com.rzb.pms.model.Generic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class GenericDto {

	@NotNull(message = "Id can't be null")
	private String genericId;

	private String name;

	private List<Drug> drugs = new ArrayList<Drug>();

	public GenericDto(Generic gen) {
		this.genericId = gen.getGenericId();
		this.name = gen.getName();
		//this.drugs = gen.getDrugs();

	}

}
