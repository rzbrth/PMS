package com.rzb.pms.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Generics {

	@Id
	private String code;

	/*
	 * @ManyToMany private Set<Drugs> drugs;
	 */

	private String genericName;

	private String groupName;

	private Boolean isGeneric;

	private String availableForms;

	private String strength;

	private String adminstrationRoute;

	private String adminstrationForm;

	private String drugGroup;

	private String interaction;

}
