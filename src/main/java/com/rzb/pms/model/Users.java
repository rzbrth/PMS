package com.rzb.pms.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.rzb.pms.dto.Address;
import com.rzb.pms.dto.Phone;

import lombok.Data;

@Data
@Entity
public class Users {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String userName;

	private String userRole;

	private String password;

	@Embedded
	private Address presentAddress;
	/*
	 * Here we are overriding default column name to user defined for presentAddress
	 * by using @AttributeOverrides
	 */
	@Embedded
	@AttributeOverrides({

			@AttributeOverride(name = "state", column = @Column(name = "HOME_STATE")),
			@AttributeOverride(name = "city", column = @Column(name = "HOME_CITY")),
			@AttributeOverride(name = "street", column = @Column(name = "HOME_STREET")),
			@AttributeOverride(name = "pincode", column = @Column(name = "HOME_PIN"))

	})
	private Address permanentAddress;

	// @formatter:off
	/*
	 * @ElementCollection will generate new table named users_phone_details automatically but its
	 * not a good table name so we defining new table name using
	 * @JoinTable(name ="USER_PHONE", joinColumns = @JoinColumn(name = "U_ID") )
	 * Her We define table name and foreign key name also explicitly. By default there will
	 * be no primary key in table USER_PHONE so we need to define one
	 * using @COllectionId
	 * 
	 */
	// @formatter:on

	
	@ElementCollection(fetch = FetchType.EAGER)
	@JoinTable(name = "USER_PHONE", joinColumns = @JoinColumn(name = "U_ID"))
	@GenericGenerator(name = "myGenerator", strategy = "sequence")
	@CollectionId(columns = { @Column(name = "PHONE_ID") }, generator = "myGenerator", type = @Type(type = "long"))
	private Collection<Phone> phoneDetails = new ArrayList<>();
}
