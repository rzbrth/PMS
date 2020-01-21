package com.rzb.pms.model;

import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import com.rzb.pms.dto.Address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Distributer {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer distributerId;

	private String name;

	@Embedded
	private Address address;

	private String email;

	private String phoneNumber;

	private String alternatePhoneNumber;
	
//	@OneToMany
//	@JoinColumn(name = "distributerId", insertable = false, updatable = false)
//    private List<PoDrug> data;
}
