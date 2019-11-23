package com.rzb.pms.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GenericPharma {

	@Id
	private String id;

	private String name;

	private String pregnancyCategory;

	private String lactationCategory;

	private String instructions;
	private String sideEffects;
	private String howItWorks;
	private String therapeuticClass;
	private String usedFor;
	private String strength;
	private String alcoholInteractionDescription;
	private Boolean alcoholInteraction;

	@OneToMany(mappedBy = "genericPharma", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<DrugPharma> drugPharma = new ArrayList<>();

}
